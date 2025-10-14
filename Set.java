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
        public DynamicSet(Object... elements){
            size = 10;
            set = new Node[size];
            unionUpdate(elements);
        }
        /**
         * Computes the hash for a given element to determine its index in the hash table.
         * The hash is calculated by summing the character values of the element's string representation.
         * @param element The element to hash.
         * @return The calculated index for the element in the hash table array.
         */
        private int hashFunction(Object element){
            int sum = 0;
            char[] charArray = element.toString().toCharArray();
            for (char chr : charArray) { 
                sum+=(int)chr;
            }
            // The modulo operator ensures the index is within the bounds of the array size.
            return sum%size;
        }
        /**
         * Adds an element to the set. If the element already exists, the set remains unchanged.
         * If the set's load factor is reached, it will be resized.
         * @param element The element to add to the set.
         */
        public void add(Object element){
            // If the number of elements reaches the capacity, resize the hash table.
            if(dataCounter>=size){
                resizeSet();
            }
            int index = hashFunction(element);
            Node currNode = set[index];
            // If the bucket at the calculated index is empty, create a new node.
            if (currNode==null){
                set[index]=new Node(element);
                dataCounter++;
                return;
            }
            // Traverse the linked list at the bucket to check for duplicates or add the new element.
            while (currNode!=null) {
                // If the element already exists, do nothing and return.
                if (currNode.value.equals(element)) {
                    currNode.value = element;
                    return;
                }
                // If we reach the end of the list, add the new element.
                else if (currNode.Next==null) {
                    dataCounter++;
                    currNode.Next = new Node(element);
                    return;
                }
                currNode=currNode.Next;
            }
        }
        /**
         * Removes an element from the set, if it is present.
         * @param element The element to remove.
         */
        public void remove(Object element){
            int index = hashFunction(element);
            Node currNode = set[index];
            // Check if the head of the list is the element to be removed.
            if (currNode != null && currNode.value.equals(element)) {
                set[index]=currNode.Next;
                dataCounter--;
                return;
            }
            // Traverse the list to find the element to remove.
            while (currNode!=null) {
                // Look ahead to find the node to remove so we can update the 'Next' pointer of the previous node.
                if(currNode.Next!=null&&currNode.Next.value.equals(element)){
                    currNode.Next = currNode.Next.Next;
                    dataCounter--;
                    return;
                }
                currNode=currNode.Next;
            }
        }
        /**
         * Updates the set by adding all elements from the given collection of elements.
         * This method modifies the set in-place. If an element from the input is already
         * present in the set, it is not added again, as the `add` method ensures uniqueness
         * and handles resizing of the set if needed.
         * @param elements A variable number of elements to add to the set.
         */
        public void unionUpdate(Object... elements){
            // Iterate through each element provided in the input.
            for (Object element : elements) {
                // The add method handles checking for duplicates and resizing if necessary.
                add(element);
            }
        }
        /**
         * Returns a new array containing the union of the set and the specified elements.
         * This method does not modify the original set.
         * @param elements The elements to form the union with.
         * @return A new array containing all unique elements from the set and the provided elements.
         */
        public Object[] union(Object...elements){
            // Calculate the number of elements shared between the set and the input.
            int sharedDataSize = 0;
            for (Object object : getDataSet()) {
                sharedDataSize = (search(elements, object)) ? (sharedDataSize+1) : sharedDataSize;
            }
            // The size of the union is the sum of sizes minus the shared part.
            int dataSize = (dataCounter+elements.length)-sharedDataSize;
            Object[] unionArray = new Object[dataSize];
            int t = 0;
            // Add all elements from the input array.
            for (Object object : elements) {
                unionArray[t] = object;
                t++;
            }
            // Add elements from the current set that are not already in the input array.
            for (Object object : getDataSet()){
                if (!search(elements, object)){
                    unionArray[t] = object;
                    t++;
                }
            }
            return unionArray;
        }
        /**
         * Updates the set, keeping only the elements found in both it and the specified elements.
         * This method modifies the set in-place by rebuilding the hash table from scratch
         * to contain only the common elements.
         * @param elements A variable number of elements to form the intersection with.
         */
        public void intersectionUpdate(Object... elements){
            // Get all elements currently in the set.
            Object[] data = getDataSet();
            // For efficiency, identify the smaller and larger of the two arrays (the current set's data and the input elements).
            // The search for common elements will be performed against these.
            Object[] arrObjects = (data.length>=elements.length) ? data : elements;
            Object[] subArrObjects = (data.length<elements.length) ? data : elements;
            int dataSize = 0;
            // First pass: Iterate through one array and search for its elements in the other to count the number of common elements.
            // This count will be the size of the new, intersected set.
            for (Object object : arrObjects) {
                dataSize = (search(subArrObjects,object)) ? (dataSize+1) : dataSize;
            }
            // Re-initialize the set's underlying hash table.
            // The new size is set to the exact number of elements in the intersection.
            size = dataSize;
            set = new Node[size];
            dataCounter = 0;
            // Second pass: Iterate through the arrays again.
            // If an element is found in both, add it to the newly initialized set.
            for (Object object : arrObjects) {
                if (search(subArrObjects, object)){
                    // The add method handles hashing the element into the new, smaller table.
                    add(object);
                }
            }
        }
        public Object[] itersection(Object...elements){
            // Count how many elements from the current set are also in the provided elements.
            int dataSize = 0;
            for (Object object : getDataSet()) {
                dataSize = (search(elements, object)) ? (dataSize+1) : dataSize;
            }
            // Create an array of the calculated size.
            Object[] unionArray = new Object[dataSize];
            int t = 0;
            // Populate the array with the common elements.
            for (Object object : getDataSet()) {
                if (search(elements, object)){
                    unionArray[t] = object;
                    t++;
                }
            }
            return unionArray;
        }
        /**
         * Updates the set, keeping only the elements not found in the specified elements.
         * This method modifies the set in-place by removing any elements that are also
         * present in the input collection. It rebuilds the hash table from scratch to
         * contain only the resulting elements.
         */
        public void differenceUpdate(Object... elements){
            // Get all elements currently in the set.
            Object[] data = getDataSet();
            int dataSize = 0;
            // First pass: Count the number of elements that are in the current set but NOT in the input elements.
            // This determines the exact size of the resulting set.
            for (Object object : data) {
                dataSize = (!search(elements, object)) ? (dataSize+1) : dataSize;
            }
            // Re-initialize the set's underlying hash table with the new, smaller size.
            dataCounter = 0;
            size = dataSize;
            set = new Node[size];
            // Second pass: Iterate through the original data again.
            // This time, add the elements that are part of the difference into the new, resized hash table.
            for (Object object : data) {
                if (!search(elements, object)){
                    // The add method handles hashing the element into the new table.
                    add(object);
                }
            }
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
        /**
         * Checks if the set contains the specified element.
         * @param element The element to search for in the set.
         * @return true if the element is found, false otherwise.
         */
        public boolean contains(Object element){
            // Find the bucket for the given element.
            int index = hashFunction(element);
            Node currNode = set[index];
            // Traverse the linked list at the calculated index.
            while (currNode!=null) {
                // If a node with the same value is found, return true.
                if (currNode.value.equals(element)){
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
        public Object[] getDataSet(){
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
         * This private helper method is called when the number of elements (`dataCounter`)
         * reaches the current capacity (`size`). It creates a new, larger array and
         * re-hashes all existing elements into the new table.
         */
        private void resizeSet(){
            // First, retrieve all existing elements from the set into a temporary array.
            Object[] data = getDataSet();

            // Double the capacity of the set.
            size*=2;
            // Create a new, empty hash table with the increased size.
            set = new Node[size];
            // Reset the element counter before re-adding elements.
            dataCounter = 0;

            // Iterate through the original data and add each element back into the new, resized set.
            // The `add` method will handle the hashing and placement into the new table structure.
            for(int i=0 ; i<data.length ; i++){
                if (data[i]!=null){
                    add(data[i]);
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
            // Create a new DynamicSet with a mix of integer and string elements.
            DynamicSet set = new DynamicSet(2,3,4,5,1,6,7,8,9,10,"11",13,12,14,15,16);
            // Attempt to add a duplicate element (1). The set should not change.
            set.add(1);
            // Perform a union update with many duplicate elements. The set should remain unchanged.
            set.unionUpdate(2,3,4,5,1,6,7,8,9,10,11,13,12,14,15,16);
            // Print the set and its length to verify its initial state.
            System.out.println(set);
            System.out.println(set.length());
            // Update the set to be the intersection of itself and {4, 5, 2, 1}.
            set.intersectionUpdate(4,5,2,1);
            // The set should now only contain {1, 2, 4, 5}.
            System.out.println(set);
            // Update the set by removing elements {4, 5, 6, 7}.
            set.differenceUpdate(4,5,6,7);
            // The set should now only contain {1, 2}.
            System.out.println(set);
            // Check if the set contains the element 2 (should be true).
            System.out.println(set.contains(2));
            // Print the final length of the set.
            System.out.println(set.length());

            // Create a second set for demonstrating union and intersection operations.
            DynamicSet set1 = new DynamicSet(2,3,4,5,6);
            // Get the union of the two sets without modifying either.
            Object[] unionArr = set.union(set1.getDataSet());
            // Get the intersection of the two sets without modifying either.
            Object[] interseptArr = set.itersection(set1.getDataSet());
            
            System.out.println();
            for (Object object : unionArr) {
                System.out.print(object+", ");
            }
            System.out.println();
            for (Object object : interseptArr) {
                System.out.print(object+", ");
            }
            System.out.println();
            System.out.println();
            for (Object object : set.getDataSet()) {
                System.out.print(object+", ");
            }
            System.out.println();
        }
    }
}
