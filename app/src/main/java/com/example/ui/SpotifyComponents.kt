package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

data class SpotifyTrack(
    val id: String,
    val name: String,
    val artist: String,
    val albumImageUrl: String? = null,
    val previewUrl: String? = null,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 30000L
)

object SavedMusicManager {
    fun getSavedTracks(context: android.content.Context): List<SpotifyTrack> {
        val prefs = context.getSharedPreferences("saved_music_prefs", android.content.Context.MODE_PRIVATE)
        val serialized = prefs.getString("saved_tracks", "") ?: ""
        if (serialized.isEmpty()) return emptyList()
        return serialized.split("##").mapNotNull {
            val parts = it.split("||")
            if (parts.size >= 3) {
                SpotifyTrack(
                    id = parts[0],
                    name = parts.getOrNull(1) ?: "",
                    artist = parts.getOrNull(2) ?: "",
                    albumImageUrl = parts.getOrNull(3)?.takeIf { it != "null" && it.isNotEmpty() },
                    previewUrl = parts.getOrNull(4)?.takeIf { it != "null" && it.isNotEmpty() },
                    trimStartMs = parts.getOrNull(5)?.toLongOrNull() ?: 0L,
                    trimEndMs = parts.getOrNull(6)?.toLongOrNull() ?: 30000L
                )
            } else null
        }
    }

    fun saveTrack(context: android.content.Context, track: SpotifyTrack) {
        val tracks = getSavedTracks(context).toMutableList()
        if (tracks.none { it.id == track.id }) {
            tracks.add(track)
            saveTracks(context, tracks)
        }
    }

    fun removeTrack(context: android.content.Context, trackId: String) {
        val tracks = getSavedTracks(context).filter { it.id != trackId }
        saveTracks(context, tracks)
    }

    fun isSaved(context: android.content.Context, trackId: String): Boolean {
        return getSavedTracks(context).any { it.id == trackId }
    }

    private fun saveTracks(context: android.content.Context, tracks: List<SpotifyTrack>) {
        val serialized = tracks.joinToString("##") {
            "${it.id}||${it.name}||${it.artist}||${it.albumImageUrl ?: ""}||${it.previewUrl ?: ""}||${it.trimStartMs}||${it.trimEndMs}"
        }
        context.getSharedPreferences("saved_music_prefs", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("saved_tracks", serialized)
            .apply()
    }
}

@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SpotifySearchDialog(
    onDismiss: () -> Unit,
    onTrackSelected: (SpotifyTrack) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<SpotifyTrack>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    val musicService = remember { com.example.data.ITunesApiService.create() }

    // Active tab state: "search" or "saved"
    var activeTab by remember { mutableStateOf("search") }
    // State to trigger local re-rendering when bookmark events happen
    var savedTrigger by remember { mutableStateOf(false) }

    // Fetch saved tracks from local manager
    val savedTracks = remember(activeTab, savedTrigger) {
        SavedMusicManager.getSavedTracks(context)
    }

    // Playback state inside search dialog
    var playingTrackId by remember { mutableStateOf<String?>(null) }
    var expandedTrackId by remember { mutableStateOf<String?>(null) }
    var trimStartSec by remember { mutableStateOf(0f) }
    var trimEndSec by remember { mutableStateOf(30f) }

    // Local controller for previews
    val previewPlayer = remember {
        com.example.ui.buildSafeExoPlayer(context).apply {
            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL
            addListener(object : androidx.media3.common.Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.w("SpotifySearch", "Preview playback error: ${error.message}")
                    try { stop() } catch (e: Exception) {}
                }
            })
        }
    }

    DisposableEffect(previewPlayer) {
        onDispose {
            previewPlayer.release()
        }
    }

    // Controls player playback & trims
    LaunchedEffect(playingTrackId, trimStartSec, trimEndSec, activeTab) {
        try {
            if (playingTrackId == null) {
                try { previewPlayer.pause() } catch (e: Exception) {}
            } else {
                val trackList = if (activeTab == "saved") savedTracks else results
                val currentTrack = trackList.find { it.id == playingTrackId }
                val url = currentTrack?.previewUrl
                if (!url.isNullOrEmpty()) {
                    try {
                        val mediaItem = androidx.media3.common.MediaItem.fromUri(android.net.Uri.parse(url))
                        previewPlayer.setMediaItem(mediaItem)
                        previewPlayer.prepare()
                        previewPlayer.seekTo((trimStartSec * 1000).toLong())
                        previewPlayer.play()
                    } catch (e: Exception) {
                        android.util.Log.e("SpotifySearch", "Error prepping player", e)
                    }

                    // Loop playback inside the trimmed range
                    try {
                        while (playingTrackId == currentTrack.id) {
                            delay(100)
                            val isPlaying = try { previewPlayer.isPlaying } catch (e: Exception) { false }
                            if (isPlaying) {
                                val currentPos = try { previewPlayer.currentPosition } catch (e: Exception) { 0L }
                                if (currentPos >= (trimEndSec * 1000).toLong() || currentPos < (trimStartSec * 1000).toLong() - 500) {
                                    try { previewPlayer.seekTo((trimStartSec * 1000).toLong()) } catch (e: Exception) {}
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Loop safely interrupted/exited or canceled
                    }
                } else {
                    playingTrackId = null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SpotifySearch", "Playback LaunchedEffect error", e)
        }
    }

    LaunchedEffect(query) {
        if (query.trim().length > 2) {
            isSearching = true
            delay(600) // Debounce
            try {
                val searchResponse = musicService.searchTracks(query)
                results = searchResponse.results.map { track ->
                    SpotifyTrack(
                        id = track.trackId.toString(),
                        name = track.trackName,
                        artist = track.artistName,
                        albumImageUrl = track.artworkUrl,
                        previewUrl = track.previewUrl
                    )
                }
            } catch (e: Exception) {
                results = emptyList()
            } finally {
                isSearching = false
            }
        } else {
            results = emptyList()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        playingTrackId = null
                        onDismiss()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        "Search Music",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Tabs for Search vs Saved Music
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabButton(
                        text = "Search Online",
                        selected = (activeTab == "search"),
                        onClick = {
                            playingTrackId = null // Pause when switching tabs
                            expandedTrackId = null
                            activeTab = "search"
                        }
                    )
                    TabButton(
                        text = "Saved Songs",
                        selected = (activeTab == "saved"),
                        onClick = {
                            playingTrackId = null
                            expandedTrackId = null
                            activeTab = "saved"
                        }
                    )
                }

                if (activeTab == "search") {
                    // Search Bar
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Song or Artist") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val currentList = if (activeTab == "saved") savedTracks else results

                if (activeTab == "search" && isSearching) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (currentList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (activeTab == "saved") "No saved tracks yet. Save songs using the bookmark icon!"
                            else if (query.trim().length > 2) "No tracks found"
                            else "Type keywords to search for tracks"
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(currentList) { track ->
                            val isSaved = SavedMusicManager.isSaved(context, track.id)
                            val isExpanded = expandedTrackId == track.id
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isExpanded) MaterialTheme.colorScheme.surfaceVariant 
                                                     else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    ListItem(
                                        headlineContent = { Text(track.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                        supportingContent = { Text(track.artist, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                        leadingContent = {
                                            if (track.albumImageUrl != null) {
                                                AsyncImage(
                                                    model = track.albumImageUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.MusicNote, contentDescription = null)
                                                }
                                            }
                                        },
                                        trailingContent = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                // Saved Bookmark Icon toggle
                                                IconButton(onClick = {
                                                    if (isSaved) {
                                                        SavedMusicManager.removeTrack(context, track.id)
                                                    } else {
                                                        SavedMusicManager.saveTrack(context, track)
                                                    }
                                                    savedTrigger = !savedTrigger
                                                }) {
                                                    Icon(
                                                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                                        contentDescription = "Bookmark Song",
                                                        tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                // Quick Play/Pause button
                                                if (!track.previewUrl.isNullOrEmpty()) {
                                                    IconButton(onClick = {
                                                        if (playingTrackId == track.id) {
                                                            playingTrackId = null
                                                        } else {
                                                            playingTrackId = track.id
                                                            if (!isExpanded) {
                                                                expandedTrackId = track.id
                                                                trimStartSec = track.trimStartMs / 1000f
                                                                trimEndSec = track.trimEndMs / 1000f
                                                            }
                                                        }
                                                    }) {
                                                        Icon(
                                                            imageVector = if (playingTrackId == track.id) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                                            contentDescription = "Play Preview",
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.clickable {
                                            if (isExpanded) {
                                                expandedTrackId = null
                                            } else {
                                                expandedTrackId = track.id
                                                trimStartSec = track.trimStartMs / 1000f
                                                trimEndSec = track.trimEndMs / 1000f
                                                playingTrackId = track.id // Auto-play when expanded
                                            }
                                        }
                                    )

                                    if (isExpanded) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.ContentCut,
                                                    contentDescription = "Trim",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Trim Song (30s Preview)",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            // Start Trim
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    "Start: ${String.format("%.1fs", trimStartSec)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.width(72.dp)
                                                )
                                                Slider(
                                                    value = trimStartSec,
                                                    onValueChange = {
                                                        trimStartSec = it
                                                        if (trimEndSec <= trimStartSec) {
                                                            trimEndSec = (trimStartSec + 1f).coerceAtMost(30f)
                                                        }
                                                        previewPlayer.seekTo((trimStartSec * 1000).toLong())
                                                    },
                                                    valueRange = 0f..29f,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }

                                            // End Trim
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    "End: ${String.format("%.1fs", trimEndSec)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.width(72.dp)
                                                )
                                                Slider(
                                                    value = trimEndSec,
                                                    onValueChange = {
                                                        trimEndSec = it.coerceAtLeast(trimStartSec + 1f)
                                                    },
                                                    valueRange = 1f..30f,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }

                                            // Clip length indicator
                                            val clipLength = trimEndSec - trimStartSec
                                            Text(
                                                text = "Selected segment length: ${String.format("%.1f", clipLength)}s",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )

                                            // Confirm Button
                                            Button(
                                                onClick = {
                                                    playingTrackId = null
                                                    onTrackSelected(
                                                        track.copy(
                                                            trimStartMs = (trimStartSec * 1000).toLong(),
                                                            trimEndMs = (trimEndSec * 1000).toLong()
                                                        )
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                )
                                            ) {
                                                Text("Attach This Segment", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpotifyTrackCard(
    trackName: String,
    trackArtist: String,
    albumImageUrl: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (albumImageUrl != null) {
                AsyncImage(
                    model = albumImageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = trackName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = trackArtist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
