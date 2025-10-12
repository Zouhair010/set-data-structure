import java.util.Arrays;
public class Set{
    /**
     * A custom implementation of a dynamic Set data structure using a hash table
     * with separate chaining for collision resolution. It supports standard set
     * operations like add, remove, and contains, as well as union, intersection and difference.
     */
    public static class DynamicSet{
        /**
         * Inner class representing a node in a linked list.
         * Used for chaining in case of hash collisions.
         */
        private static class Node{
            Node Next = null;
            Object value;
            /**
             * Constructs a Node with the given value.
             * @param value The value to be stored in the node.
             */
            public Node(Object value){
                this.value = value;
            }
        }
        // The underlying array for the hash table. Each element is the head of a linked list.
        private Node[] set;
        // The current capacity of the hash table array.
        private int size;
        // The number of elements currently stored in the set.
        private int dataCounter = 0;
        /**
         * Constructs an empty Set with a default initial capacity of 10.
         */
        public DynamicSet(){
            size = 10;
            set = new Node[size];
        }
        /**
         * Computes the hash for a given element to determine its index in the hash table.
         * The hash is calculated by summing the character values of the element's string representation.
         * @param elem The element to hash.
         * @return The calculated index for the element in the hash table array.
         */
        private int hashFunction(Object elem){
            int sum = 0;
            char[] charArray = elem.toString().toCharArray();
            for (char chr : charArray) { 
                sum+=(int)chr;
            }
            // The modulo operator ensures the index is within the bounds of the array size.
            return sum%size;
        }
        /**
         * Adds an element to the set. If the element already exists, the set remains unchanged.
         * If the set's load factor is reached, it will be resized.
         * @param elem The element to add to the set.
         */
        public void add(Object elem){
            // If the number of elements reaches the capacity, resize the hash table.
            if(dataCounter>=size){
                resizeSet();
            }
            int index = hashFunction(elem);
            Node currNode = set[index];
            // If the bucket at the calculated index is empty, create a new node.
            if (currNode==null){
                set[index]=new Node(elem);
                dataCounter++;
                return;
            }
            // Traverse the linked list at the bucket to check for duplicates or add the new element.
            while (currNode!=null) {
                // If the element already exists, do nothing and return.
                if (currNode.value.equals(elem)) {
                    currNode.value = elem;
                    return;
                }
                // If we reach the end of the list, add the new element.
                else if (currNode.Next==null) {
                    dataCounter++;
                    currNode.Next = new Node(elem);
                    return;
                }
                currNode=currNode.Next;
            }
        }
        /**
         * Removes an element from the set, if it is present.
         * @param elem The element to remove.
         */
        public void remove(Object elem){
            int index = hashFunction(elem);
            Node currNode = set[index];
            // Check if the head of the list is the element to be removed.
            if (currNode != null && currNode.value.equals(elem)) {
                set[index]=currNode.Next;
                dataCounter--;
                return;
            }
            // Traverse the list to find the element to remove.
            while (currNode!=null) {
                // Look ahead to find the node to remove so we can update the 'Next' pointer of the previous node.
                if(currNode.Next!=null&&currNode.Next.value.equals(elem)){
                    currNode.Next = currNode.Next.Next;
                    dataCounter--;
                    return;
                }
                currNode=currNode.Next;
            }
        }
        /**
         * Updates the set, adding elements from an iterable or another set.
         * @param elems A variable number of elements to add to the set.
         */
        public void unionUpdate(Object... elems){
            // Iterate through all provided elements and add them to the set.
            for (Object elem : elems) {
                // This check is somewhat inefficient as it's inside the loop.
                // A single resize check before the loop would be better.
                if(dataCounter>=size){
                    resizeSet();
                }
                int index = hashFunction(elem);
                Node currNode = set[index];
                // If the bucket is empty, add the new element.
                if (currNode==null){
                   set[index]=new Node(elem);
                   dataCounter++;
                   continue;
                }
                // Traverse the linked list to handle collisions or find duplicates.
                while (currNode!=null) {
                    if (currNode.value.equals(elem)) {
                        // Element already exists, do nothing.
                        currNode.value = elem;
                        break;
                    }
                    else if (currNode.Next==null) {
                        dataCounter++;
                        currNode.Next = new Node(elem);
                        break;
                    }
                    currNode=currNode.Next;
                }
            }
        }
        /**
         * Updates the set, keeping only the elements found in both it and the specified elements.
         * This method rebuilds the hash table from scratch.
         * @param elems A variable number of elements to form the intersection with.
         */
        public void intersectionUpdate(Object... elems){
            // Get all elements currently in the set.
            Object[] data = getData();
            // Determine which array is larger to optimize the search loop.
            Object[] arrObjects = (data.length>=elems.length) ? data : elems;
            Object[] subArrObjects = (data.length<elems.length) ? data : elems;
            int dataSize = 0;
            // First pass: Count the number of elements in the intersection.
            for (Object object : arrObjects) {
                if (search(subArrObjects, object)){
                    dataSize++;
                }
            }
            // Create a new hash table with a size equal to the intersection count.
            Node[] newSet = new Node[dataSize];
            // Second pass: Populate the new hash table with the intersection elements.
            for (Object object : arrObjects) {
                if (search(subArrObjects, object)){
                    int sum = 0;
                    // Manually re-calculating the hash. This duplicates the hashFunction logic.
                    char[] charArray = object.toString().toCharArray();
                    for (char chr : charArray) { 
                        sum+=(int)chr;
                    }
                    int index = sum%dataSize;
                    Node currNode = newSet[index];
                    if (currNode==null){
                        // No collision, insert as the head of the list.
                        newSet[index]=new Node(object);
                        continue;
                    }
                    while (currNode!=null) {
                        if (currNode.value.equals(object)) {
                            currNode.value = object;
                            break;
                        }
                        // Add to the end of the list.
                        else if (currNode.Next==null) {
                            currNode.Next = new Node(object);
                            break;
                        }
                        currNode=currNode.Next;
                    }
                }
            }
            // Replace the old set, counter, and size with the new ones.
            set = newSet;
            dataCounter = dataSize;
            size = dataSize;
        }
        /**
         * Updates the set, keeping only the elements not found in the specified elements.
         * This method rebuilds the hash table from scratch.
         */
        public void differenceUpdate(Object... elems){
            // Get all elements currently in the set.
            Object[] data = getData();
            int dataSize = 0;
            // First pass: Count the number of elements in the difference.
            for (Object object : data) {
                if (!search(elems, object)){
                    dataSize++;
                }
            }
            // Create a new hash table with a size equal to the difference count.
            Node[] newSet = new Node[dataSize];System.out.println(set);
            // Second pass: Populate the new hash table with the difference elements.
            for (Object object : data) {
                if (!search(elems, object)){
                    int sum = 0;
                    // Manually re-calculating the hash. This duplicates the hashFunction logic.
                    char[] charArray = object.toString().toCharArray();
                    for (char chr : charArray) { 
                        sum+=(int)chr;
                    }
                    int index = sum%dataSize;
                    Node currNode = newSet[index];
                    if (currNode==null){
                        // No collision, insert as the head of the list.
                        newSet[index]=new Node(object);
                        continue;
                    }
                    while (currNode!=null) {
                        if (currNode.value.equals(object)) {
                            currNode.value = object;
                            break;
                        }
                        // Add to the end of the list.
                        else if (currNode.Next==null) {
                            currNode.Next = new Node(object);
                            break;
                        }
                        currNode=currNode.Next;
                    }
                }
            }
            // Replace the old set, counter, and size with the new ones.
            set = newSet;
            dataCounter = dataSize;
            size = dataSize;
        }
        /**
         * A helper method to perform a linear search for an object within an array.
         * @param objects The array to search in.
         * @param object The object to search for.
         * @return true if the object is found, false otherwise.
         */
        private boolean search(Object[] objects, Object object){
            for (Object obj : objects) {
                if (obj.equals(object)){
                    return true;
                }
            }
            return false;
        }
        public boolean contains(Object elem){
            // Find the bucket for the given element.
            int index = hashFunction(elem);
            Node currNode = set[index];
            // Traverse the linked list at the calculated index.
            while (currNode!=null) {
                // If a node with the same value is found, return true.
                if (currNode.value.equals(elem)){
                    return true;
                }
                currNode=currNode.Next;
            }
            return false;
        }
        /**
         * Returns the number of elements in the set.
         * @return The count of elements in the set.
         */
        public int length(){
            return dataCounter;
        }
        /**
         * Retrieves all elements from the set and returns them as an array.
         * @return An array of Objects containing all elements in the set.
         */
        public Object[] getData(){
            Object [] data = new Object[dataCounter];
            int t = 0;
            // Iterate through each bucket of the hash table.
            for(int i=0 ; i<set.length ; i++){
                Node currNode = set[i];
                // Traverse the linked list in the current bucket.
                while (currNode!=null) {
                    data[t] = currNode.value;
                    t++;
                    currNode = currNode.Next;
                }
            }
            return data;
        }
        /**
         * Resizes the hash table to double its current capacity.
         * This involves creating a new, larger array and re-hashing all existing elements.
         */
        private void resizeSet(){
            // Temporarily store all existing elements.
            Object[] data = new Object[dataCounter];
            int counter = 0;
            for (Node node : set) {
                Node currnode = node;
                while (currnode!=null) {
                    data[counter++] = currnode.value;
                    currnode = currnode.Next;
                }
            }
            // Double the size and create a new hash table.
            size*=2;
            set = new Node[size];
            // Re-hash and re-insert all elements into the new, larger table.
            for(int i=0 ; i<data.length ; i++){
                int index = hashFunction(data[i]);
                Node currNode = set[index]; 
                if (currNode==null){
                    // No collision, insert as the head of the list.
                    set[index]=new Node(data[i]);
                    continue;
                }
                while (currNode!=null) {
                   if(currNode.value.equals(data[i])){
                       currNode.value = data[i];
                       break;
                    }
                    // Add to the end of the list.
                    else if(currNode.Next==null){
                       currNode.Next = new Node(data[i]);
                       break;
                    }
                    currNode=currNode.Next;
                }
            }
        }
        /**
         * Returns a string representation of the set.
         * @return A string in the format "{elem1, elem2, ...}".
         */
        @Override
        public String toString(){
            String[] data = new String[dataCounter];
            int t = 0;
            // Iterate through each bucket of the hash table.
            for (int i=0 ; i < set.length ; i++) {
                Node currNode = set[i];
                // Traverse the linked list in the current bucket.
                while (currNode!=null) {
                    data[t] = typeFormat(currNode.value);
                    t++;
                    currNode = currNode.Next;
                }
            }
            return String.format("{%s}", String.join(", ", data));
        }
        /**
         * Formats an object as a string for display, adding quotes for characters and strings.
         * @param item The object to format.
         * @return A formatted string representation of the object.
         */
        private static String typeFormat(Object item){
            String string;
            switch (item.getClass().getName()) {
                    case "java.lang.Character":
                        string = "\'"+item+"\'";
                        break;
                    case "java.lang.String":
                        string = "\""+item+"\"";
                        break;
                    default:
                        string = ""+item;
                        break;
            }
            return string;
        }
        /**
         * Main method for demonstrating the Set class functionality.
         */
        public static void main(String[] args){
            DynamicSet set = new DynamicSet();
            set.add(1);
            set.unionUpdate(2,3,4,5,1);
            System.out.println(set);
            System.out.println(set.length());
            set.intersectionUpdate(4,5,2,1);
            System.out.println(set);
            set.differenceUpdate(4,5,6,7);
            System.out.println(set);
            System.out.println(set.contains(2));
            System.out.println(set.length());
        }
    }
}