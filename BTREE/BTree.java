public class BTree implements BTreeInterface {
	private BNode root;
	private final int t;

	/**
	 * Construct an empty tree.
	 */
	public BTree(int t) { //
		this.t = t;
		this.root = null;
	}

	// For testing purposes.
	public BTree(int t, BNode root) {
		this.t = t;
		this.root = root;
	}

	@Override
	public BNode getRoot() {
		return root;
	}

	@Override
	public int getT() {
		return t;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		result = prime * result + t;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BTree other = (BTree) obj;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		if (t != other.t)
			return false;
		return true;
	}
	
	@Override
	public Block search(int key) {
		return root.search(key);
	}

	@Override
	public void insert(Block b) {
		if(root==null)
			root=new BNode(t,b);
		else{
			if (root.isFull()){
				BNode s= new BNode(t, root); //now s is the new root
				root=s;
				s.splitChild(0);
			}
			root.insertNonFull(b);	
		}		
	}

	@Override
	public void delete(int key) {
		if(search(key)!=null)
                    root.delete(key);
        if(root.getNumOfBlocks()==0&&!root.isLeaf())
        	root=root.getChildAt(0);
	}


	@Override
	public MerkleBNode createMBT() {
		return root.createHashNode();
	}


}
