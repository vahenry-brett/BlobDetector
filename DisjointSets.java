//Author: Brett Henry
//CS310 Project 4
import java.util.ArrayList;
import java.util.Iterator;

//disjoint sets class, using union by size and path compression.
public class DisjointSets<T>
{
	private int[] s; //the sets
	private ArrayList<Set<T>> sets; //the actual data for the sets

	/**
	 * Constructor takes an List of data and creates a set for each individual element
	 * @param data - the list of data
	 */
	public DisjointSets(ArrayList<T> data) {
		s = new int[data.size()];
		sets = new ArrayList<Set<T>>();
		for(int x = 0; x < data.size(); x++)
		{
			Set<T> toAdd = new Set<T>();
			toAdd.add(data.get(x));
			sets.add(toAdd);
			s[x] = -1;
		}
	}

	
	/**
	 * Computes the union of two sets by size, if the sets are equal in size, root1 becomes the new root. 
	 * @param root1, root2
	 * @returns the new root
	 * @throws IllegalArgumentException for roots given that are equal or not actual roots
	 */
	public int union(int root1, int root2) {
		if(s[root1] >= 0 || s[root2]>=0 || root1 == root2)
		{
			System.out.println("root 1 is:"+root1+" root 2 is:"+root2+" s[root1] is "+s[root1]+" s[root2] is: "+s[root2]);
			throw new IllegalArgumentException();
		}
		if(s[root1] == s[root2] || s[root1]<s[root2])
		{
			//System.out.println("In here");
			s[root1] += s[root2];
			s[root2] = root1;
			sets.get(root1).addAll(sets.get(root2));
			sets.get(root2).clear();
			return root1;
		}
		s[root2] += s[root1];
		s[root1] = root2;
		sets.get(root2).addAll(sets.get(root1));
		sets.get(root1).clear();
		return root2; //TODO: remove and replace this line
	}

	/**
	 * Finds and returns the root of the element at the given index. Uses path compression.
	 * @param x - the index of the element to find the root of
	 * @returns the root
	 */
	private void assertIsItem( int x )
	{
		if( x < 0 || x >= s.length )
			throw new IllegalArgumentException( );
	}
	public int find(int x) {
		assertIsItem( x );
		if( s[ x ] < 0 )
			return x;
		else
			return s[ x ] = find( s[ x ] );
	}

	/**
	 * Gets and returns all of the data int the set at the given root
	 * @param root, the root of the set to get
	 * @return Set<T> the set
	 */
	public Set<T> get(int root) {
		return sets.get(root); //TODO: remove and replace this line
	}
	
	//main method just for your testing
	public static void main(String[] args) {
		ArrayList<Integer> arr = new ArrayList<>();
		for(int i = 0; i < 100; i++)
		{
			//arr.add(-1*i);
			arr.add(i);
		}
		DisjointSets<Integer> ds = new DisjointSets<>(arr);
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(1)); //should be 1
		System.out.println(ds.union(0, 1)); //should be 0
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(1)); //should be 0
		System.out.println("-----");
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(2)); //should be 2
		System.out.println(ds.union(2, 0)); //should be 0
		System.out.println(ds.find(0)); //should be 0
		System.out.println(ds.find(2)); //should be 0
		System.out.println("-----");
		System.out.println(ds.union(3, 0));
		System.out.println(ds.find(3));
		ds.union(0, 75);
		ds.union(0, 76);
		ds.union(0, 77);
		ds.union(0, 78);
		System.out.println(ds.find(78));
		//Note: AbstractCollection provides toString() method using the iterator
		//see: https://docs.oracle.com/javase/8/docs/api/java/util/AbstractCollection.html#toString--
		//so your iterator in Set needs to work for this to print out correctly
		System.out.println("This is 0:"+ds.get(0)); //should be [0, 1, 2]
		System.out.println(ds.get(1)); //should be []
		System.out.println(ds.get(3)); //should be [3]
	}
}
