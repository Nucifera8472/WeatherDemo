# WeatherDemo

## How to build
Add your OpenWeatherMap api key to the `local.properties` file:
`apiKey=yourKeyHere`

## Technology stack

- MVVM
- Repository pattern
- Kotlinx serialization
- Mockk
- Dagger+Hilt for DI
- Shared element fragment transitions to go from forecast preview tile to forecast full page detail 

## Known bugs / Issues

- `misc.xml` which is supposed to be in source control changes all the time when using the design preview https://issuetracker.google.com/issues/192128532
- The layout is currently not optimized for smaller screens.
- Wind direction icon glitches during shared element transition

## Attribution
<a href="https://www.flaticon.com/free-icons/weather" title="weather icons">Weather icons created by Those Icons - Flaticon</a>
<a href="https://www.flaticon.com/free-icons/sun" title="sun icons">Sun icons created by Freepik - Flaticon</a>
