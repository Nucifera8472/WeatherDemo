# WeatherDemo

## How to build
Add your OpenWeatherMap api key to the `local.properties` file:
`apiKey=yourKeyHere`

## Design / Architecture decisions

- Decided to go with the onecall endpoint from OpenWeatherMap to reduce the amount of API calls to a minimum

- Using the retrofit - kotlinx serialization converter factory in combination with the retrofit coroutine support to directly retrieve the deserialized result from the retrofit endpoint.

- Originally started implementing dependency injection with Dagger-android, but then changed my mind because the solution seemed very 2018 to me and finally tried out Hilt for the first time. The realization that Hilt has the view model factory I was using several years now under the hood already and additionally makes many of my current Dagger modules obsolete was very satisfying.

- For many years I used LiveData in repositories as was recommended by Google. I now know of the problems (e.g. when using transformations which are performed on main thread in this case) and try to use kotlin flow instead. For simple use cases like in this app, choosing LiveData over kotlin flow is still a valid option, as it is less complex and directly tied to the Android life cycle, which kotlin flow isn't.

- The initial version uses png weather condition icons in only 1 size. This is not good practice and for a production app I would definitely go with vector graphics. (the icons I decided on were only downloadable in vector graphics format with a premium subscription which I didn't want to purchase for a demo app)

- The weather API response is not directly used in the app but instead converted to an app internal model. This separates the app from eventual changes on the endpoint as well as opens up the option to have user selectable weather services (which have a minimum common subset of provided data points to make the app usable, but can offer additional premium features)

- Fetched weather data is cached in a local database to be shown (if it is still current enough) while new data is fetched. This also makes it possible to switch between multiple saved locations quickly without having to refetch data on every switch.

- The most tricky part is to decide when weather data is too old to be shown at all. (user is offline) Depending on geographical location, forecast data has a high probability to be correct for the next 2 or 3 days. Showing the current weather for more than 2 hours might be irrelevant, as during sunrise/sunset the measured temperature can change a lot, and during storm season the weather can change dramatically within a short time period.

- The layout is currently not optimized for smaller screens.

## Known bugs

- `misc.xml` which is supposed to be in source control changes all the time when using the design preview https://issuetracker.google.com/issues/192128532


## Attribution
<a href="https://www.flaticon.com/free-icons/weather" title="weather icons">Weather icons created by Those Icons - Flaticon</a>
