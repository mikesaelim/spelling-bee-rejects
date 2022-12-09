# spelling-bee-rejects

The [Spelling Bee game](https://www.nytimes.com/puzzles/spelling-bee) at the New York Times
is great, but everyone can come up with words that didn't make it onto the official list of answers.
What words got rejected?

There already exist [sites](https://nytbee.com/) that do this, but even they leave out more
modern inventions like "bakeable" or "assholeish". So I figured that extracting a word list
from the [English Wiktionary](https://en.wiktionary.org/) would give me the most expansive
vocabulary.

This Java application currently just runs locally, and has two functions: generating a word
list from the English Wiktionary, and using that word list to solve a Spelling Bee prompt.
It is in a very hacky and janky state right now, because I haven't decided what to turn this
into yet.


### How to use

#### Generate a word list from the English Wiktionary

First, download the latest English Wiktionary article dump from [their site](https://en.wiktionary.org/wiki/Help:FAQ#Downloading_Wiktionary).
You should be looking for a file named `enwiktionary-latest-pages-articles.xml.bz2`. 
Verify its md5 checksum, and unzip the dump to get an XML file with all the articles.

Run 

```./gradlew clean run --args="-g <path to xml file>"```

to generate the word list, which goes to `data/wordlist.txt` by default. If you want to change
the word list path, use `./gradlew clean run --args="-g <path to xml file> --wordlist <word list path>"`.

#### Solve a Spelling Bee

Run 

```./gradlew clean run --args=a:ciedhk```

where in this example `a` is the central letter and `ciedhk` are the remaining 6 letters in
the prompt. This uses a word list located at `data/wordlist.txt` by default. If you want to
change the word list path, use `./gradlew clean run --args="a:ciedhk --wordlist <word list path>"`.
