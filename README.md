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

#### Mile Stone --- Week 6

Talk With Demai:

1. Current Photo is 300+, need to grow to 1000+

2. The Select Test of the month and time range, one of them should be implemented by using the Realm, equal""

3. Find the usage of the Memory between Realm and Sqlite when executing the app

4. The DiskSpace/ find the db file and the compare

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
