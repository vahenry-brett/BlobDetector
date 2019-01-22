# BlobDetector
A program that detects blobs of a given color in images written in Java.
Compile with javac *.java


Run as: java BlobDetection image_file -k K -r red_value -g green_value -b blue_value -d distance [-o image_file_output] [-show]

Where K is the number of blobs you want to find, red_value blue_value, and green_value are each colors corresponding rgb value of the color blob you want to look for and distance is the how close each pixels color must be to the given color to be included in the blob. 
