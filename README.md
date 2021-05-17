# BFST 21 - Group 2
This repository contains the source code for the BFST 21 project by group 2. The group consists of:
|Initials | Name |
|--|--|
| emio |  Emil Østergaard |
| frev | Freja Bruun Vangaard |
| korg | Jeppe Korgaard | 
| jacj | Jacob Møller Jensen |
| ibsc | Ib Løwe Skovfoged Scherer |


## Contributing guidelines
All code must be formatted using the default java formatter.

### Running the code
1. Clone the repository
2. Open a terminal and cd into the folder
3. Run `./gradlew run`

#### Possible issues
##### Permission denied
If you run into the following permission error on a unix system (macOS or Linux):
```gradlew: permission denied```

You need to make the file executable by running:
```bash
$ chmod +x gradlew
```

##### Cannot lock java compile cache
If you get an error message that contains the following line:
```
   > Cannot lock Java compile cache
```
The java compiler is most likely locked by a different `gradle` process. Stop the running gradle process with:
```bash
$ ./gradlew --stop
```
And try again


### Running tests
Tests can be run by the following command `./gradlew test`

Test data sets can be found at the google drive folder: https://drive.google.com/drive/folders/1R7fnfzYdkQTKBd0vnwLvxTNq2OviLFE2?usp=sharing 

### Running performance tests

#### TTI
The time to interactive test is run exclusively on UNIX system, since we rely on the `time` command to measure the TTI (time to interactive)

To run the test on your own machine run the following command: 
```bash
$ time ./gradlew run --args="ttiMode"
```

The program will run and exit immediately when the window is ready to handle user input. The output of the command will look something like this:
```bash
./gradlew run --args="ttiMode"  1,11s user 0,07s system 43% cpu 2,728 total
```

The number we're interested in is the total (the last one).

### Running Denmark
You need to allocate 6 GB RAM to run the file denmark.osm.zip, this is done by specifying (...) in build.gradle