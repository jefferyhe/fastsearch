fastsearch
==========
This project contains two main parts, preprocess the files and then do the search.

1.  Performed less than 1 second time cost by searching an item from 1T random files or even more
 
2. Preprocess the files by an external sorting mechanism and built index for the files


3. Implemented a binary search function based on the address of the item in disk, which means doesn't need to read the file into memory. In this way, we can performe a very fast search

###Preprocessing:
1.  In the SortFile class, the sortFiles function takes two arguments:  input folder and output folder. Which sort every file in the input folder by Java.util.TreeMap class. Then write the sorted files to a “temp” output folder.

2.  In the MergerSort class, wraps a BinaryFileBuffer class on top of a BufferedReader which keeps the last line in memory. Maintains a heap with 20 (depends on the number of input files) BinaryFileBuffer objects, use merge sort algorithm, keep write into output files in Processed folder. If the number of lines reach the number of lines of the original file, create a new file and continue to write. Finally we will have the same number of output files as input.

3.  Build a HashMap using the path of the processed file as key, and the range of the file as value, store the hashmap into a file in "processed" folder as “hash.txt”. 

###Searching: 
1.  Read the hashmap into memory, loop the hashmap by check to which name range the input “name” belongs , if find, we can easily get the target file by its hash function.

 
2.  Open the target file in RandomAcessFile and then perform a binary search by using seek() function,  which doesn’t need to read the whole file into memory. Shortly, we can get the phone number associated to the input name.   
