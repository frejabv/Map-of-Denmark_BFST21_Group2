# BFST 21 - Group 2
This repository contains the source code for the BFST 21 project by group 2. The group consists of:
|Initials | Name |
|--|--|
| emio |  Emil Østergaard |
| frev | Freja Bruun Vangaard |
| korg | Jeppe Korgaard | 
| jacj | Jacob Møller Jensen |
| ibsc | Ib Løwe Skovfoged Scherer |


### Running the code
1. Clone the repository
2. Open a terminal and cd into the folder
3. Run `./gradlew run`

#### Possible issues
##### Permission denied
If you run into a the following permission error on a unix system (macOS or Linux):
```gradlew: permission denied```

You need to make the file executable by running
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

