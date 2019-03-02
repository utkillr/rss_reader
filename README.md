# rss_reader
Small project which implements simple RSS Reader

## Launch
```
mvn clean package
cd target
java -jar rssfeed-1.0.jar
```
## Command examples
```
rss add https://www.nasa.gov/rss/dyn/breaking_news.rss <path>\nasa.txt 10
rss channel https://www.nasa.gov/rss/dyn/breaking_news.rss
rss item https://www.nasa.gov/rss/dyn/breaking_news.rss
rss item https://www.nasa.gov/rss/dyn/breaking_news.rss title description pubdate
rss time 300
rss add https://www.nasa.gov/rss/dyn/shuttle_station.rss <path>\nasa_shuttle.txt
rss
rss off https://www.nasa.gov/rss/dyn/shuttle_station.rss
rss
save
exit
```
