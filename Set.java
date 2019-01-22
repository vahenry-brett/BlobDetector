// Author: Brett Henry
// CS310 Project 4

import java.util.AbstractCollection;
import java.util.Iterator;

public class Set<T> extends AbstractCollection<T> {
	//O(1)
	private class node<T>{
		public node<T> next;
		public T data;
		
		public node(T data) {
			next = null;
			this.data = data;
		}
	}
	public node<T> head;
	public node<T> tail;
	private int size;
	public Set() {
		head = tail = null;
		size = 0;
	}
	
	/**
	 * Adds a node to the set
	 * @param item
	 * @returns true if successfull, false if not
	 */
	public boolean add(T item) {
		node<T> toAdd = new node<T>(item);
		if(head == null)
			head = tail = toAdd;
		else
		{
			tail.next = toAdd;
			tail = toAdd;
		}
		size++;
		return true; 
	}
	
	/**
	 * Adds all of the elements from the given set to this set
	 * @param other - the other set
	 * @returns true upon success, false otherwise
	 */
	public boolean addAll(Set<T> other) {
		this.tail.next =  other.head;
		this.tail = other.tail;
		other.head = this.head;
		this.size += other.size();
		return true;
	}
	
	/**
	 * Clears all of the elements from the set
	 */
	public void clear() {
		head = tail = null;
	}
	
	/**
	 * Gets and returns the size of the set
	 * @return size - the size of the set
	 */
	public int size() {
		return size;
	}
	
	/**
	 * creates and returns an iterator the moves through the set in sequential order
	 * @returns Iterator
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			//O(1)
			node<T> current = head;
			public T next() {
				T temp = current.data;
				current = current.next;
				return temp; 
			}
			
			//O(1)
			public boolean hasNext() {
				if(current == null)
					return false;
				return true;
			}
		};
	}
	
	/**
	 * main method for testing
	 */
	public static void main(String[] args) {
		Set<Integer> set1 = new Set<Integer>();
		Set<Integer> set2 = new Set<Integer>();
		Set<Integer> set3 = new Set<Integer>();
		for(int x = 0; x<10; x++)
		{
			set1.add(x);
			set2.add(0-x);
			set3.add(x*10);
		}
		System.out.println(set1);
		System.out.println(set3);
		set1.addAll(set2);
		set1.addAll(set3);
		/*Iterator<Integer> s1 = set1.iterator();
		while(s1.hasNext())
		{
			System.out.println(s1.next());
		}*/
		System.out.println(set1);
		
	}
}
