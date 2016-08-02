# Your Secret Tracker

#### MileStone 1 --- Week 1 
Get familiar with Realm.io and retrieve the GPS data from the Pictures/

#### MileStone 2 --- Week 2
Retrieve the Location Data from the GPS info in the background, included Zipcode and State and City

#### MileStone 3 --- Week 3 

Implemented a SeekBar TimeLine that could allow the user to search the photo by touching across the bar, but doesn't 
Could display the location of photo on the map and implement a photo outlook cluster with the number of items on the icon

#### MileStone 4 --- Week 4 
- The unseen part should not be generated(still try to implement)
- Build a test that record the time spent on select specific month/time range

Talk With Demai:

1. Separate the loop of fetchAddress when loading the photo's information to the database. Keep the location data in an array or hash map and fetch the data directly from array or hash map.

2. In the ShowTimeLine.java: Construct 3 test, each perform 1000 times, use a Random Generator to generate the query month/specific time range. Random Generate the Latitude/ Logitude (Point on Map) and select out the photo that are within range to the random point

3. Build the App using SQLite, perform the above procedure and calculate the time.

#### MileStone 5 --- Week 5 

- Test the query of selecting the nearest photo to a random place 
- Build the App using SQLite and perform the same tests again

#### Mile Stone 6 --- Week 6
Finished the first draft of the poster, wondering what will be the next step for this project 

Talk With Demai:

1. Current Photo is 300+, need to grow to 1000+

2. The Select Test of the month and time range, one of them should be implemented by using the Realm, equal""

Solution: Implement the test "Equal" by finding the places equal to Evanston or Chicago

3. Find the usage of the Memory between Realm and Sqlite when executing the app

4. The DiskSpace/ find the db file and the compare(508 photos)
Realm: 147KB  SQlite: 61KB

#### MileStone 7 --- Week 7 

Talk With Demai: 

1. better to get a couple more data points, such as 1000 photos and 2000 photos. so that we are able to draw a plot and make the comparison  predictable . the same idea apply to other part measurement. (disk space, memory, speed)
2. recognize a particular object, start with facial.

#### MileStone 8 --- Week 8

My Intention:
1. Filter out the photos with faces in them 
2. Tag the photo's with faces in them and tag each person:?-- ( Need the training? (To tag manually or could implement it automatically if we have enough database?)
3. How to implement the "Friend" Function:
Suppose that you are friend with each other on the app and If we found that groups of you are tagged in a photo, and we could generate a friendship diary of " One XXX(Date),  you guys(...) are having fun at XXX (The location data)" The GPS and the timestamp data could be retrieved from the Realm database.

What I found out about different API:

The android API is not trustworthy, even by increasing the size of the photo, it could hardly detect whether a person exist in the photo. Especially when the face is small.. 

The Microsoft version still is not effective!

Google Cloud Version: Better then the above API but it just describe the object it sees and it also cannot locate the faces as Facebook’s API. It’s not face oriented?

#####The IMPROVEMENT: 
Imagine there are many user's photo library, how to recoginize that maybe you guys are at the same time when
you don't even know each other. What's more, implement the the ML face recogintion in your photos (?)will there be security problem involved. And how to redesign the database to demonstrate the users. 
Tap on the Map, shows the nearest place you've been to 
- No need to reload the photos everytime, figure out how to discover that the photos in the folder have been changed and asked 
the user to choose whether to update their current database or not. 
- Group the photos into catogory, place a little icon like food/shopping center/school/work/scenary 
"Show places that I have eaten at" on the Map and filter out the pictures in that category 

#### The Presentation --- Last Week
What is your Algorithms, How to demo your App, Any other Chart or data to show the Comparison?
