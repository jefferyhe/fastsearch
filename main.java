class main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        long startTime = System.currentTimeMillis();

        String phone = search("Betty", "Baker");
        System.out.println(phone);

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("searching time: " + totalTime);
    }
}