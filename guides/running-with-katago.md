# Running Lizzie with KataGo

I've seen many posts asking for help on how to run KataGo with Lizzie and I thought I'll write a post about it. Feel free to contribute to this guide as you try to follow along and get your system running.

In this guide I'm using Katago version 1.6.1 and 1.7.0. I'm sure it works with many more versions. For Lizzie I'm using something around version 0.7.4. As I've been compiling my own Lizzie for some time then I use my own build but it is not that far off from 0.7.4.

I will assume that you have already done some work and you have:

- You have KataGo configured (see [Setting Up and Running KataGo](https://github.com/lightvector/KataGo#setting-up-and-running-katago) for details)
- You have Lizzie running (see [Running Lizzie](https://github.com/featurecat/lizzie#running-lizzie) for details)

The next steps are to define the Engine in Lizzie. Before we start doing that we should come up with a proper KataGo startup command that we'll later on add to Lizzie configuration.

I personally use 2 different setups. One is when I'm at home and I have access to my other machine with a more powerful GPU and the the other option is running KataGo on my Mac where I'm also running Lizzie from. I'll call these two respectively Local and Remote configurations.

Once we have prepared the command and configured the engine we'll also do a sample analysis of a game!

Alright, onto prepareing the command!

## Prepare the Command

### Local Configuration

KataGo requires a model file and a configuration file to run and Lizzie is talking to KataGo over GTP ([Go Text Protocol](https://en.wikipedia.org/wiki/Go_Text_Protocol)) we also need to specify the protocol.

So overall the minimal command will look like this

```katago gtp -model my-model-file.txt.gz -config my-configuration.cfg```

Of course this simple example will look for ```katago``` from the current folder which won't work with Lizzie. The same is true for the configuration and the model file.

My personal full command looks like this
```/Users/toomasr/projects/KataGo/cpp/katago gtp -model /Users/toomasr/Downloads/g170-b30c320x2-s4824661760-d1229536699.bin.gz -config /Users/toomasr/projects/KataGo/cpp/configs/gtp_example.cfg```

As you can see I've actually downloaded the model file to my ```Downloads``` folder. You can find updated models for KataGo at their [releases](https://github.com/lightvector/KataGo/releases) page. You might have to click on ```Assets``` for a release to actually see the list of models. All KataGo relaeses are not model releases so scroll around a bit. I usually take either the largest model file or the second one in size.

For the KataGo configuration file I use the one that comes with KataGo and looks like this [gtp_example.cfg](https://github.com/lightvector/KataGo/blob/master/cpp/configs/gtp_example.cfg)

I personally recommend testing the commandline in a terminal before starting to configure Lizzie. Also note that the first time you run the command it might print more things on the screen as it will do some tuning and then save the tuning settings and skip this process next time.

This is what I see when I run the command on my machine.

![Running the KataGo Command](https://github.com/toomasr/lizzie/blob/master/guides/images/katago-commandline.png?raw=true)

I have included 2 screenshots. One is from the terminal where I prefer to run Lizzie from and the other one is the GTP console from inside Lizzie. Your output will vary but this is what it looks like for a successful setup.

### Remote Configuration

The remote configuration is fairly similar. You need to have a working command at the other machine and you need to test it there. Once it is working on the remote machine I just use SSH to run the command over there.

I've defined a wrapper ```katago-analysis-remote.sh``` which has the following contents

```bash
#!/bin/bash

ssh toomasr@192.168.1.131 "/home/toomasr/projects/KataGo/cpp/katago gtp -config /home/toomasr/projects/KataGo/cpp/configs/analysis_example.cfg"
```

Now for this to work you need to make sure that this command works. First of all you need non-interactive SSH authentication. This [SSH Public Key Auth Tutorial](https://kb.iu.edu/d/aews) looks good enough to get you started.

![Running the KataGo Remote Command](https://github.com/toomasr/lizzie/blob/master/guides/images/katago-remote-commandline.png?raw=true)

## Configure Lizzie to Use the Command
	
We now have a command that will launch KataGo locally or remotely and next step is to use this command to setup an engine inside the Lizzie interface. Head over to ```Settings``` menu and open ```Engine``` from there.

![Managing Engines in Lizzie](https://github.com/toomasr/lizzie/blob/master/guides/images/lizzie-engines-configuration.png?raw=true)

On the screenshot I have defined 3 engines. The second engine is a local KataGo and the third one is my remote KataGo.

Once you have these defined you can head over to ```Engines``` in the main menu and you should see a list. They are numbered by the numbers in the engines configuration from the previous step.

![Lizzie Output](https://github.com/toomasr/lizzie/blob/master/guides/images/lizzie-output.png?raw=true)

![GTP Output](https://github.com/toomasr/lizzie/blob/master/guides/images/gtp-console.png?raw=true)

## Analyze a Game
	
Now that you have everything working you can start analysing a game. I've been using Lizzie for a year but as the program has very many options I believe I'm using just a small subset. I'll explain my main flows in the analysis and this should be enough to get you going and discovering the app more on your own.

Whenever I play a game online and I have some time afterwards I download the SGF file from the server. Then I open it up on Lizzie with ```File``` &rarr; ```Open```.

Then I start the engine from the ```Engine``` menu.

Once the engine has loaded and initialized I navigate to ```Analyze``` &rarr; ```Auto analyze(A)```. Lizzie will ask how many playouts should it perform (higher the better but will have diminishing returns, at least on my level). I usually go for 500 or up to a 1000 depending on how much time I have. On my remote machine with GeForce RTX 2070 it takes about 3 minutes to analyse a game with 500 playouts.

Once the game is analyzed I start going over it. I first look at the big mistakes. You can spot these either from the WinRateGraph or on the movepane moves that are colored red. Once I find them I also like to look at alternative moves. The moves that KataGo suggests are of course great but on my level I actually need to look at some dumber moves that KataGo has not pre analysed for me. For those I play a move on the board and then use the SPACE key on my keyboard to either turn on or off the Pondering mode of Katago.

![Example Analysis](https://github.com/toomasr/lizzie/blob/master/guides/images/full-game-analysis.png?raw=true)

Roughtly this is it. Auto analyse a game and then go over the game either move by move or by highlights/lowlights from the WinRateGraph.