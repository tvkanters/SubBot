# SubBot
A simple tool for downloading subtitles of films and series and fixing episode names on Windows.

To use:
* Compile as runnable JAR with `com.tvkdevelopment.subbot.InstallerSubBot` as main class.
* Place the JAR somewhere where it's not gonna be moved.
* Run the JAR to add right click commands to the registry.<sup>1</sup>
* Right click movies to download subtitles for it or folders to treat all contained video files as a series season.

![](http://i.imgur.com/LCUjKzC.png)

<sup>1</sup> May not always work due to Windows registry being chaotic and confusing. I haven't looked into how to get add a shell command reliably to a file. WinAMP in particular seems to break things.

Special thanks to [OpenSubtitles](http://www.opensubtitles.org/) and [TvMaze](http://www.tvmaze.com/) for their great APIs.
