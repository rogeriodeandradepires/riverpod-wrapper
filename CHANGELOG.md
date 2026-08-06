<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Riverpod Wrapper Changelog

## [Unreleased]

## [1.1.1] - 2026-08-06

### Changed
- Fixed the release pipeline so a released version is published to the JetBrains Marketplace and the installable plugin zip is attached to the GitHub release.

## [1.1.0] - 2026-07-29

### Changed
- Support for Android Studio Quail (2026.1, build 261) and IntelliJ Platform 261.
- Removed the `until-build` upper bound so the plugin stays installable on newer IDE releases.
- Migrated the build to the IntelliJ Platform Gradle Plugin 2.x, Gradle 9.6.1, Kotlin 2.3.20 and Java 21.
- Replaced the deprecated `StartupActivity` with `ProjectActivity`.

## [1.0.0] - 2024-05-17

### Added
- Initial release of the Riverpod Wrapper plugin.
- Functionality to wrap Flutter widgets into Hooks Riverpod's widgets: Consumer and HookConsumer.
- Intention actions to convert a ConsumerWidget to ConsumerStatefulWidget and vice versa.
- Intention actions to convert a HookConsumerWidget to StatefulHookConsumerWidget and vice versa.
- Functionality to remove or unwrap specific widgets like Consumer and HookConsumer while preserving their child content.

[Unreleased]: https://github.com/rogeriodeandradepires/riverpod_wrapper/compare/v1.1.1...HEAD
[1.1.1]: https://github.com/rogeriodeandradepires/riverpod_wrapper/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/rogeriodeandradepires/riverpod_wrapper/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/rogeriodeandradepires/riverpod_wrapper/commits/v1.0.0
