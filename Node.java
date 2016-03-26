
package multidocument;

import java.util.HashSet;

public class Node {
    
	
    public double rank = 0.0D;
    public String key = null;
    public NodeValue value = null;

    private Node (final String key, final NodeValue value) {
	this.rank = 1.0D;
	this.key = key;
	this.value = value;
    }

    public static Node buildNode (final Graph graph, final String key, final NodeValue value) throws Exception {
	
    Node n = graph.get(key);

	if (n == null) {
	    n = new Node(key, value);
	    graph.put(key, n);
	}
	return n;
    }
}
