I graduated, so there is no reason to continue this.

# EOSClientLauncher

## Info

* This is a simple program to download the EOS Client
* I've seen the news that you need to re-download the client on every exam days, which is hilarious
* I'm lazy, so I made my own tool to automatically download it

## Download

* Download the jar file from [here](https://github.com/HSGamer/EOSClientLauncher/releases/latest)

## Requirement

* Java 8 or newer

## Set Up

0. Create your Drive project and get its Client ID and Client Secret
    * [The Tutorial](https://theonetechnologies.com/blog/post/how-to-get-google-app-client-id-and-client-secret)
1. Download the jar file
2. Create a run script named `start.bat` at the same directory as the jar file
3. Add this line to `start.bat`
```bat
java -jar eos-cli.jar
```
3. Save `start.bat`
4. Run `start.bat`, It should fail in the first time (because we didn't setup the client settings)
5. On the newly `config.yml` on the same directory
    * Set the value of `client.id` to your client's ID
    * Set the value of `client.secret` to your client's secret key
    * Edit some options to the way you want
6. Save it and run again
7. On the first run, the program will open a page asking you to fill your account. You should enter the account that has
   access to the Shared Drive of FPT University
7. Once you filled, a token will be saved to the program and the downloading process will be started.
8. Now, on everytime you run the program, you only have to choose the file you want to launch and the program will download and launch it automatically
