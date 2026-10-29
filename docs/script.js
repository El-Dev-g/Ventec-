document.addEventListener('DOMContentLoaded', () => {
    const downloadBtn = document.getElementById('downloadBtn');
    
    if (downloadBtn) {
        downloadBtn.addEventListener('click', (e) => {
            // Optional: Add analytics or tracking here before the download starts
            console.log('APK download initiated');
        });
    }
});
