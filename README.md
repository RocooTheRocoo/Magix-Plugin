Magix
======

Magix is a whitehat malicious-plugin attempt for dev.bukkit.org
At first sight the code looks safe but in fact it includes a force op. This
op can be achieved by typing in: '?.=opmePls password is HolyMoly'

The source and binaries can be found at the "Releases". Please do not use this
code for malicious purposes. It is provided here so these kind of "hacks" can be
avoided in the future.



Reddit post:

BukkitDev should no longer be considered safe
==============================================

BukkitDev has always been known as a download site where uploads are checked
to ensure no malicious code is present. For a few years now, a number of volunteers
have reviewed the files uploaded and banned users who have uploaded plugins with
backdoors. Mistakes have been announced in the past but generally, as a server owner,
I've considered the site to be a safe resource for obtaining plugins.

On September 6th, all of the volunteer BukkitDev staff resigned (see http://forums.bukkit.org/threads/an-independent-goodbye.310086/
for more details). Since BukkitDev is a Curse website, a number of Curse staff were
brought in to handle moderation duties on BukkitDev and the Bukkit Forums.

Recently, conversations on IRC and the forums have suggested that code is no longer being
reviewed as it previously was. Threads such as http://forums.bukkit.org/threads/misleading-plugins.316758/
present a vague picture of possible issues. Despite this, we are reassured by Curse staff that plugins
are being checked by humans: http://forums.bukkit.org/threads/how-approval-is-going-now.312644/#post-2815487

I wanted to test the new moderation. To do so, I wrote a plugin which allows admins to script an item.
(Source and jar are both available at: https://github.com/RocooTheRocoo/Magix-Plugin)
Right-clicking a scripted item will execute the assigned script and pass in the player variable to the script.
This makes code such as `player.sendMessage(org.bukkit.ChatColor.BLUE + 'hello');` possible.

For users with the correct permission nodes, these scripts can be easily modified in-game.

At first sight, there's nothing wrong with my plugin. Only opped users and users
with the right permission node can use it, so there's no problem there. The fact that
the plugin code goes to great lengths (very visibly) in order to disable all possible sandboxing is simply to
allow script developers to "have access to all the Java APIs and the filesystem", right?

Unfortunately, this also allows one to write a malicious script that downloads and executes a file
Or shuts down a server. Or while we're at it, make a server part of a huge botnet. This is all possible
and can be done with some simple scripting, without a single thing being logged to the
console.

But you can only use the plugin when you're an OP, so what's the problem?

The problem is within the statement "You can only use it when you're an OP". It's true, but only to an extent.
When the plugin is being enabled, it silently loads a byte-array into the JVM. Basically
just defining a class from a byte-array. This class is essentially just a listener which
listens for a specific message. Once this message is typed in, it will OP the user.
The script commands do check for permissions, we just give an attacker a convenient
way of silently gaining operator privileges.

And that's where stuff gets nasty.

Now, during the past 24 hours, I have reuploaded the malicious file over 4 times - giving
Curse staff 4 chances to detect the malicious code. They *should* have noticed it. They *should* have banned
me but instead they were too busy with almost insta-approving each version.

Please feel free to use this WebCitation link: http://www.webcitation.org/6TUmGwttm
That link shows a snapshot of the file page. The "semi-normal" status shows it has been approved.
Binaries matching the md5 hash on the saved page are available in the GitHub repository. The
project page snapshot is available at http://www.webcitation.org/6TUollWmN

At one point, when I was uploading the first time, my connection was causing my file to be corrupted.
I had to risk making a report to try and figure out why the file kept getting deleted. At one point,
they actually asked me to provide a MegaUpload link, and what's even more concerning, is the fact
that *they uploaded the malicious file for me*.

Screenshots of this report: https://cdn.mediacru.sh/Zefza_-Cp38a.png https://cdn.mediacru.sh/zm3stYth1EXl.png

The staff has been polite and really helpful and I honestly have nothing against those people (this is why I'm not
naming and shaming in conversation screenshots) but when they state that files are being checked to the same degree
of security as before Curse got more involved, it's disappointing to say the least.

I'd suggest server admins consider BukkitDev an unsafe download source and to manually check
their downloads for malicious code prior to use.

- Rocoo
