package multidocument;

import java.io.File;

import opennlp.tools.lang.english.ParserTagger;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.util.Sequence;
import org.tartarus.snowball.ext.englishStemmer;
import spiaotools.SentParDetector;


/**
 * Implementation of English-specific tools for natural language
 * processing.
 *
 * @author Kush & Kanika
 */

public class Utility //extends Opennlp
{
    public final static int TOKEN_LENGTH_LIMIT = 50;
    public static SentParDetector splitter_en = null;
    /** /
    public static SentenceDetectorME splitter_en = null;
    /* */
    public static Tokenizer tokenizer_en = null;
    public static ParserTagger tagger_en = null;
    public static englishStemmer stemmer_en = null;


    /**
     * Constructor. Not quite a Singleton pattern but close enough
     * given the resources required to be loaded ONCE.
     */

    public
	Utility (final String path)
	throws Exception
    {
	if (splitter_en == null) {
	    loadResources(path);
	}
    }


    /**
     * Load libraries for OpenNLP for this specific language.
     */

    public void
	loadResources (final String path)
	throws Exception
    {
    	System.out.println("Path " + path);
	splitter_en = new SentParDetector();

	tokenizer_en =
	    new Tokenizer((new File("C:/Automatic-Text-Summarizer-master/Automatic-Text-Summarizer-master/OpenNlp/EnglishTok.bin.gz")).getPath());
		System.out.println("path" + path);

	tagger_en =
	    new ParserTagger((new File("C:/Automatic-Text-Summarizer-master/Automatic-Text-Summarizer-master/OpenNlp/tag.bin.gz")).getPath(),
			     (new File("C:/Automatic-Text-Summarizer-master/Automatic-Text-Summarizer-master/OpenNlp/tagdict")).getPath(),
			     false
			     );

	stemmer_en =
	    new englishStemmer();
    }


     public String[]
	splitParagraph (final String text)
    {
	return splitter_en.markupRawText(2, text).split("\\n");

    }



    public String[]
	tokenizeSentence (final String text)
    {
	final String[] token_list = tokenizer_en.tokenize(text);

	for (int i = 0; i < token_list.length; i++) {
	    token_list[i] = token_list[i].replace("\"", "").toLowerCase().trim();
	}

	return token_list;
    }


    /**
     * Run a part-of-speech tagger on the sentence token list.
     */

    public String[]
	tagTokens (final String[] token_list)
    {
	final Sequence[] sequences = tagger_en.topKSequences(token_list);
	final String[] tag_list = new String[token_list.length];

	int i = 0;

	for (Object obj : sequences[0].getOutcomes()) {
	    tag_list[i] = (String) obj;
	    i++;
	}

	return tag_list;
    }



    public String
	getNodeKey (final String text, final String pos)
        throws Exception
    {
	return pos.substring(0, 2) + stemToken(scrubToken(text)).toLowerCase();
    }


    public String
        scrubToken (final String token_text)
        throws Exception
    {
        String scrubbed = token_text;

        if (scrubbed.length() > TOKEN_LENGTH_LIMIT) {
            scrubbed = scrubbed.substring(0, TOKEN_LENGTH_LIMIT);
        }

        return scrubbed;
    }


	/**
     * Determine whether the given PoS tag is a noun.
     */

    public boolean
	isNoun (final String pos)
    {
	return pos.startsWith("NN");
    }


    /**
     * Determine whether the given PoS tag is an adjective.
     */

    public boolean
	isAdjective (final String pos)
    {
	return pos.startsWith("JJ");
    }
    
    public boolean isRelevant (final String pos) {
    	return isAdjective(pos) || isNoun(pos);
        }

    public boolean
	isVerb (final String pos)
    {
	return pos.startsWith("VB");
    }

    public String
	stemToken (final String token)
    {
	stemmer_en.setCurrent(token);
	stemmer_en.stem();

	return stemmer_en.getCurrent();
    }
}
