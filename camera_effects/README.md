# Zooz Camera Effects & AR Filters

Welcome to the **Zooz Camera Effects & AR Filters SDK** repository. Inspired by Spark AR (Meta) and TikTok Effect House, this repository defines the schemas, assets, and shaders used by spatial creators to publish creative 3D or dynamic filters for user-generated videos, stories, and streams.

## Core Features
1. **Face Mesh Tracking Template**: Standard glTF formats and mesh reference guides for overlaying 2D makeup, skin textures, and 3D glasses/hats.
2. **Segmenting Shader Library**: GLSL shaders to easily manipulate backgrounds, introduce cosmic dust particles, or blur stream borders.
3. **Interactive Audio-Reactive Effects**: Logic interfaces to let filters scale, pulse, or morph dynamically in real-time syncing of whatever soundtrack is chosen from the **Music DSP** library.
4. **Ad-Sponsored Lenses**: Integration hooks allowing commercial brands inside the **Ads Manager** to easily deploy branded lenses for user-submitted content challenges.

## Directory Structure
- `/templates` - Reference projects for Blender, Spark AR, and Canvas tools.
- `/shaders` - Dynamic OpenGL/Vulkan lighting models for real-time mobile graphics.
- `/effects-sdk` - High-performance Native C++/NDK bindings for real-time video stream overlay processing.
