# HeartBeat
An app which on giving value of Beats per Minute(BPM) would show hearbeating animation as per the BPM and also plot realtime Graph

# Library Support


Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Copy

Step 2. Add the dependency

	dependencies {
	        compile 'com.github.Shaggy2606:HeartBeat:-SNAPSHOT'
	}


# API for sending values
https://heartbeatit.herokuapp.com/api?bpm=
