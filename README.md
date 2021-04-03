# EOSClientDownloader
## Info
* This is a simple program to download the EOS Client
* I've seen the news that you need to re-download the client on every exam days, which is hilarious
* I'm lazy, so I made my own tool to automatically download it
## Download
* Download the jar file from [here](https://github.com/HSGamer/EOSClientDownloader/releases/latest)
## How to setup
0. Create your Drive project and get its Client ID and Client Secret
    * [The Tutorial](https://theonetechnologies.com/blog/post/how-to-get-google-app-client-id-and-client-secret)
1. Run the program, It should fail in the first time (because we didn't setup the client settings)
2. On the newly `config.yml` on the same directory
    * Set the value of `client.id` to your client's ID
    * Set the value of `client.secret` to your client's secret key
3. Save it and run again
4. On the first run, the program will open a page asking you to fill your account. You should enter the account that has access to the Shared Drive of FPT University
5. Once you filled, a token will be saved to the program and the downloading process will be started.
6. Now, on everytime you run the program, the file will be downloaded automatically
