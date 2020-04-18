# Rotaract Matching System

The Rotaract Matching System will facilitate the matching of Rotarians and Rotaractors to institute a mentorship program across the Ascension MDIO's service area.


## Start Guide

1. Clone the repository
1. Set 'JAVA_HOME' as an environment variable pointing to the location of a Java JDK (8 or higher)
1. Run ``./gradlew bootRunDev`` in the base directory 'rotaract_matching_system' (by default)
	1. This will build and run the system
	1. The server will now be running
	1. The swagger documentation is located at 'https://localhost:8080/'
1. Make a 'post' request to 'https://localhost:8080/data/initDefaults/'
	1. This will initialize the database based on the following:
		* The JSON file representing the questions
		* The Rotarian response csv file
