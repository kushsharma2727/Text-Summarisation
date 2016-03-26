
package multidocument;

import java.util.HashMap;

public class
    Sentence
{
    public String text = null;
    public String[] token_list = null;
    public HashMap<String, Integer> tokens = null;

    public
	Sentence (final String text)
    {
	this.text = text;
	this.tokens = new HashMap<String, Integer>();;
    }



    public void
	mapTokens (final Utility lang, final Graph graph)
	throws Exception
    {
	token_list = lang.tokenizeSentence(text);

	// scan each token to determine part-of-speech

 	final String[] tag_list = lang.tagTokens(token_list);
 	
	for (int i = 0; i < token_list.length; i++) {
	    final String pos = tag_list[i];
	    //System.out.println("THE TOKEN LIST:::" + tag_list[i]);   
	    if (lang.isRelevant(pos)) {
		final String key = lang.getNodeKey(token_list[i], pos);
		final KeyWord value = new KeyWord(token_list[i], pos);
		final Node n = Node.buildNode(graph, key, value);
        //System.out.println("Node ::: " + n.value.text);
        tokens.put(n.value.text, 1);
      }
	}
	
	  }
}
