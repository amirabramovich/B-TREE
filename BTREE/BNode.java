import java.util.ArrayList;

public class BNode implements BNodeInterface {

	private final int t;
	private int numOfBlocks;
	private boolean isLeaf;
	private ArrayList<Block> blocksList;
	private ArrayList<BNode> childrenList;

	/**
	 * Constructor for creating a node with a single child.<br>
	 * Useful for creating a new root.
	 */
	public BNode(int t, BNode firstChild) {
		this(t, false, 0);
		this.childrenList.add(firstChild);
	}

	/**
	 * Constructor for creating a <b>leaf</b> node with a single block.
	 */
	public BNode(int t, Block firstBlock) {
		this(t, true, 1);
		this.blocksList.add(firstBlock);
	}

	public BNode(int t, boolean isLeaf, int numOfBlocks) {
		this.t = t;
		this.isLeaf = isLeaf;
		this.numOfBlocks = numOfBlocks;
		this.blocksList = new ArrayList<Block>();
		this.childrenList = new ArrayList<BNode>();
	}

	// For testing purposes.
	public BNode(int t, int numOfBlocks, boolean isLeaf,
			ArrayList<Block> blocksList, ArrayList<BNode> childrenList) {
		this.t = t;
		this.numOfBlocks = numOfBlocks;
		this.isLeaf = isLeaf;
		this.blocksList = blocksList;
		this.childrenList = childrenList;
	}

	@Override
	public int getT() {
		return t;
	}

	@Override
	public int getNumOfBlocks() {
		return numOfBlocks;
	}

	@Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public ArrayList<Block> getBlocksList() {
		return blocksList;
	}

	@Override
	public ArrayList<BNode> getChildrenList() {
		return childrenList;
	}

	@Override
	public boolean isFull() {
		return numOfBlocks == 2 * t - 1;
	}

	@Override
	public boolean isMinSize() {
		return numOfBlocks == t - 1;
	}
	
	@Override
	public boolean isEmpty() {
		return numOfBlocks == 0;
	}
	
	@Override
	public int getBlockKeyAt(int indx) {
		return blocksList.get(indx).getKey();
	}
	
	@Override
	public Block getBlockAt(int indx) {
		return blocksList.get(indx);
	}

	@Override
	public BNode getChildAt(int indx) {
		return childrenList.get(indx);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blocksList == null) ? 0 : blocksList.hashCode());
		result = prime * result
				+ ((childrenList == null) ? 0 : childrenList.hashCode());
		result = prime * result + (isLeaf ? 1231 : 1237);
		result = prime * result + numOfBlocks;
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
		BNode other = (BNode) obj;
		if (blocksList == null) {
			if (other.blocksList != null)
				return false;
		} else if (!blocksList.equals(other.blocksList))
			return false;
		if (childrenList == null) {
			if (other.childrenList != null)
				return false;
		} else if (!childrenList.equals(other.childrenList))
			return false;
		if (isLeaf != other.isLeaf)
			return false;
		if (numOfBlocks != other.numOfBlocks)
			return false;
		if (t != other.t)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "BNode [t=" + t + ", numOfBlocks=" + numOfBlocks + ", isLeaf="
				+ isLeaf + ", blocksList=" + blocksList + ", childrenList="
				+ childrenList + "]";
	}
	
	
	@Override
	public Block search(int key) {
		int indx=0;
		while(indx<numOfBlocks&&getBlockKeyAt(indx)<key)
			indx++;
		if(indx<numOfBlocks&&getBlockKeyAt(indx)==key)
			return getBlockAt(indx);
		else if(isLeaf())
			return null; //block was not found.
		else
			return getChildAt(indx).search(key);
	}

        
        public void splitChild(int childIndex){//split child with 2t-1 blocks to 2 nodes with t-1 blocks and index is their dad
		BNode y=getChildAt(childIndex);
		BNode z= new BNode(t, y.isLeaf, t-1);
		for(int j=t+1;j<=2*t-1;j++)
			z.blocksList.add(y.blocksList.get(j-1));
		if(!y.isLeaf())
			for(int j=t+1;j<=2*t;j++)
				z.childrenList.add(y.getChildAt(j-1));
		y.numOfBlocks=t-1;
		childrenList.add(childIndex+1, z);
		blocksList.add(childIndex,y.getBlockAt(t-1));
		numOfBlocks=numOfBlocks+1;	
		for(int j=2*t-2; j>t-2;j--)
			y.blocksList.remove(j);
		for(int j=2*t-1; j>t-1&&!y.isLeaf();j--)
			y.childrenList.remove(j);
	}
        
	@Override
	public void insertNonFull(Block d) { //regular insertion when node isn't full
		int i=numOfBlocks-1;
		if(isLeaf()){
			while(i>=0&&d.getKey()<getBlockKeyAt(i)){
				i--;	
			}
		blocksList.add(i+1,d);
		numOfBlocks++;
		}
		else{//case has kids
			while(i>=0&&d.getKey()<getBlockKeyAt(i))
				i--;
			i++;
			if(this.getChildAt(i).isFull()){
				splitChild(i);
				if(d.getKey()>getBlockKeyAt(i))
					i++;				
			}
			getChildAt(i).insertNonFull(d);
		}		
	}
        
        public void Merge(int i) {//i is the index of the block which be merged with kids  
            //assume both kids has t-1 keys
            BNode left=getChildAt(i);
            BNode right=getChildAt(i+1);
            left.blocksList.add(this.getBlockAt(i));
            left.numOfBlocks=left.numOfBlocks+1;
            for(int j=0;j<right.numOfBlocks;j++){
                left.blocksList.add(right.getBlockAt(j));
                left.numOfBlocks=left.numOfBlocks+1;
            }
            if(!right.isLeaf()){ //transfer the kids too
                for(int j=0;j<right.numOfBlocks+1;j++){
                    left.childrenList.add(right.getChildAt(j));
                }
                for(int j=0;j<left.numOfBlocks-1;j++)//special case when the kid of kid bigger than kid
                	if(left.getBlockKeyAt(j)>left.getChildAt(j+1).getBlockKeyAt(0)){
                    	Block kid=left.getChildAt(j+1).getBlockAt(0);
                    	left.getChildAt(j).blocksList.add(kid);
                    	left.getChildAt(j).numOfBlocks++;
                    	left.getChildAt(j+1).blocksList.remove(0);
                    	left.getChildAt(j+1).numOfBlocks--;
                    }
            }
            this.blocksList.remove(i); //remove last items which weren't removed in loops
            this.childrenList.remove(i+1);
            numOfBlocks--;
        }
        
        private void shiftFromRightSiblingOf(int childIndx){ //takes block from right brother of childIndx
            BNode post=getChildAt(childIndx+1);
            getChildAt(childIndx).blocksList.add(blocksList.get(childIndx));
            getChildAt(childIndx).numOfBlocks++;
            blocksList.set(childIndx, post.getBlockAt(0));
            if(!getChildAt(childIndx).isLeaf()){
                getChildAt(childIndx).childrenList.add(post.getChildAt(0)); 
                post.childrenList.remove(0);
            }   
            post.blocksList.remove(0);
            post.numOfBlocks--;
        }
        
        private void shiftFromLeftSiblingOf(int childIndx){//takes block from left brother of childIndx
            BNode pre=getChildAt(Math.max(childIndx-1,0));//in case its in 0 index
            getChildAt(childIndx).blocksList.add(0,blocksList.get(Math.max(childIndx-1,0)));
            getChildAt(childIndx).numOfBlocks++;
            blocksList.set(Math.max(childIndx-1,0), pre.getBlockAt(pre.numOfBlocks-1));
            if(!getChildAt(childIndx).isLeaf()){
                getChildAt(childIndx).childrenList.add(0,pre.getChildAt(pre.numOfBlocks)); 
                pre.childrenList.remove(pre.numOfBlocks);
            }     
            pre.blocksList.remove(pre.numOfBlocks-1);
            pre.numOfBlocks--;
        }

        
        private void mergeWithLeftSibling(int childIndx){
            Merge(childIndx-1);
        }
        
        private void mergeWithRightSibling(int childIndx){
            Merge(childIndx);
        }
        
        private int findMinKey(){
            if(isLeaf())
            	return this.getBlockKeyAt(0);
            return getChildAt(0).findMinKey();  	
        }
        
        private int findMaxKey(){
            if(isLeaf())
            	return getBlockKeyAt(numOfBlocks-1);
            return getChildAt(numOfBlocks).findMaxKey();  	
        }
        private int sonContainsKey(int indx,int key){ //return index of son node which contains the key
        	if(getBlockKeyAt(indx)>key)
        		return (indx);
        	return (indx+1);
        }
        
        private int indxInsideNode(int key){ //return the index of key in the array or the index of his root
        int i=0;
        for(Block B: blocksList){
        	if(B.getKey()==key)
        		return i;
        	else if(B.getKey()>key)
        		return Math.max(i-1, 0); //for special case if tree with 1 root and the key in left son
        	i++;
        	}  
        return numOfBlocks-1;
        }
        
        
	@Override
	public void delete(int key) {
            int i=indxInsideNode(key);  
            if(key==blocksList.get(i).getKey()){//if key inside this node
            	if(isLeaf()){
            		blocksList.remove(i);
                    numOfBlocks=numOfBlocks-1;
                }
                else if(getChildAt(i).numOfBlocks>t-1) {//if has kids and the left one has more than t-1 keys
                        Block pre=search(getChildAt(i).findMaxKey());
                        delete(pre.getKey());
                        blocksList.set(i, pre);
                }
                    else if(getChildAt(i+1).numOfBlocks>t-1) {//if has kids and the right one has more than t-1 keys
                        Block post=search(getChildAt(i+1).findMinKey());
                        delete(post.getKey());
                        blocksList.set(i, post);
                    }
                    else{ //both kids has t-1 keys
                         Merge(i);
                         getChildAt(i).delete(key);
                    }             
                }           
                if (search(key)!=null){ //if key wasn't in the current node, make a search if has it in the tree at all
                	int sonIndx=sonContainsKey(i,key);//index of the father/owner of the key
                	if(getChildAt(sonIndx).getNumOfBlocks()==t-1){//if father/owner has t-1 keys
                		if(sonIndx>0&&getChildAt(sonIndx-1).getNumOfBlocks()>t-1)//check if can borrow key from left brother
                			shiftFromLeftSiblingOf(sonIndx);
                		else if(sonIndx<getChildAt(sonIndx).getNumOfBlocks()-(t-2)&&getChildAt(sonIndx+1).getNumOfBlocks()>t-1)//check if can borrow key from right brother
                			shiftFromRightSiblingOf(sonIndx);
                		else if(sonIndx<getChildAt(sonIndx).getNumOfBlocks()-(t-2)&&getChildAt(sonIndx+1).getNumOfBlocks()==t-1)//check if can merge with right brother
                			mergeWithRightSibling(sonIndx);
                		else if(sonIndx>0&&getChildAt(sonIndx-1).getNumOfBlocks()==t-1){//check if can merge with left brother
                			mergeWithLeftSibling(sonIndx); 
                			sonIndx--;//index is updated due to merging          				
                		}
                		else Merge(i);//if any of brothers missing or not enough keys makes regular merge
                	}
                    getChildAt(sonIndx).delete(key); //reccursive deletion key in the child  
                }               
	}
	
	public byte[] getLeafHashValue(){//check value of leaf according formula
		ArrayList<byte[]> dataList=new ArrayList<byte[]>();
		for(int i=0;i<numOfBlocks;i++)		
			dataList.add(getBlockAt(i).getData());
		return HashUtils.sha1Hash(dataList);
	}
	
	public byte[] getNodeHashValue(){//check value of node according formula with reccursion
		if(isLeaf())
			return getLeafHashValue();
		ArrayList<byte[]> dataList=new ArrayList<byte[]>();
		dataList.add(getChildAt(0).getNodeHashValue());
		for(int i=0;i<numOfBlocks;i++){				
			dataList.add(getBlockAt(i).getData());	
			dataList.add(getChildAt(i+1).getNodeHashValue());
		}
		return HashUtils.sha1Hash(dataList);
	}
	

	@Override
	public MerkleBNode createHashNode() { //building the tree in reccursion using 2 functions above
		if(isLeaf())
			return new MerkleBNode(getLeafHashValue());
		ArrayList<MerkleBNode> childrenList=new ArrayList<MerkleBNode>();
		for(int i=0;i<=numOfBlocks;i++)
			childrenList.add(getChildAt(i).createHashNode());
		return new MerkleBNode(getNodeHashValue(), childrenList);
	}
	
	

}
