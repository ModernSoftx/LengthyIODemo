# LengthyIODemo

Lab Threading
Objectives:

    Understand threading in Android
    Create threads and Runnables
    Maintain main thread operation
    Prevent main thread lock up
    Create multiple threads
    Understand the purpose of handlers
    Implement a thread handler
    Coordinate UI thread with other threads
    Pass messages to a handler
    Create a thread pool
    Implement Callable tasks and Futures

Description:
This app will simulate how to make an application unresponsive. Then discuss how to use thread and runnable to prevent it from happening. Move on to demonstrate how to coordinate threads with main thread to have the UI updated. Finally talk about how to pass data from separate thread back to UI thread in an asynchronous approach.

View binding is not used in this lab

Turn off AutoConnection to Parent on the Layout Editor

Step 1: Start a project, LengthyIODemo, by using Empty Activity template and make sure Java is the language for this project.
Step 2: Create a button with "Submit" as its text underneath “Hello World” textView in the layout file (i.e., activity_main.xml).
Step 3: Arrange constraints in the layout file so that there is no error.
Step 4: Run the app to make sure your project is without any error so far.
Step 5: Create two class variables within MainActivity.java as follows:
TextView             textView;
Button                  button;
Make sure you import the library when you create any new objects. This is a general reminder. I might not remind you from now on.
Step 6: Wire up the two variables defined at Step 5 with the objects in the layout file and associate an onClickListener with the “Submit” button.
Step 6.1: Create a method, buttonClick(), to deal with on click event in order to simulate a lengthy IO operation
public void buttonClick(View view){
long endTime = System.currentTimeMillis() + 10 * 1000;
while (System.currentTimeMillis() < endTime){
synchronized(this){
try{
wait(endTime – System.currentTimeMillis());
} catch(Exception e){}
}
}
textView.setText(“Button Clicked”);
}
Step 6.2: In onCreate(), put the following statements behind setContentView()
textView = findViewById(R.id.textView);
button = findViewById(R.id.button);
button.setOnClickListener(new View.OnClickListener(){
@Override
public void onClick(View view){ buttonClick(view); }
});
Step 7: Run the app, click the button and see the Hello World text replaced by “Button Clicked”. Now click the button several times to see the popup that indicates the app is not responsive. Take the screenshot with the error dialog.
The app is not responsive because the main thread (i.e., io thread) is working on the “lengthy” io operation.

Step 8: We will create a separate thread to handle the lengthy task so that it will not block the main thread.
Step 8.1: Create a Runnable in buttonClick() method and pass it to the start() of a thread. Notice since we don’t want to update the UI from the separate thread, we will deal with the update later on. Now your buttonClick() should look like the following:
Runnable runnable = new Runnable(){
@Override
public void run() {
long endTime = System.currentTimeMillis() + 10 * 1000;
while (System.currentTimeMillis() < endTime){
synchronized(this){
try{
wait(endTime - System.currentTimeMillis());
}catch(Exception e){}
}
}  
}
};

Thread myThread = new Thread(runnable);
myThread.start();
Step 9: Run the app and click the button several times. No error dialog is shown.
For simplicity lambda expression is not used in this lab. You can use Java lambda expression in this lab if you know how to do it.
Step 10: Implement a handler in main thread to make updates to the user interface (UI).
Step 10.1: Create a handler with call back method, handleMessage.
Notice this is declared in the variable declaration area and not in any method.
You can put this behind where you declared button at Step 5.
Handler handler = new Handler(Looper.getMainLooper()){
@Override
public void handleMessage(Message msg) {
textView.setText("Message Received");
}
};
The handleMessage creates a callback for other worker threads to communicate with the main thread.
Step 10.2: Insert a send message in buttonClick() to update the UI as follows:
Runnable runnable = new Runnable(){
@Override
public void run() {
long endTime = System.currentTimeMillis() + 10 * 1000;
while (System.currentTimeMillis() < endTime){
synchronized(this){
try{
wait(endTime - System.currentTimeMillis());
}catch(Exception e){}
}
}
handler.sendEmptyMessage(0);
}
};

Thread myThread = new Thread(runnable);
myThread.start();

Empty message is used in this step to test out the idea.
Step 11: Compile and run the app and take a screenshot to show the text view is updated with “Message Received.”
Step 12: In the prior step we send an empty message. Now let’s expand the lab to pass data through a message to the handler.
Step 12.1: Prepare data in buttonClick()
Let's replace the statement handler.sendEmptyMessage(0); we put in Step 10 by the following statements:  
Message msg = handler.obtainMessage();
Bundle bundle = new Bundle();
bundle.putString("myKey", "Thread Completed");
msg.setData(bundle);
handler.sendMessage(msg);

Pay attention you just replace handler.sendEmptyMessage(0) and keep other statements untouched.
Step 12.2: Update handler to extract data and display it on the TextView object. The handler defined at the declaration area should look like the one as follows:
Handler handler = new Handler(Looper.getMainLooper()){
@Override
public void handleMessage(Message msg) {
Bundle bundle = msg.getData();
String string = bundle.getString("myKey");
textView.setText(string);
}
};
Step 13: Run the app and take a screenshot with "Thread Completed" in the TextView.
In order to utilize thread efficiently, we will introduce Executor framework below.
Step 14: Use Executors to create a thread pool and pass Runnable task to submit() of ExecutorService.
Step 14.1: Declare a variable with the type of ExecutorService in the variable area
ExecutorService executor;
Step 14.2: Make buttonClick() look like as the following. shutdown() is used here to illustrate how to stop executor service when you don't need it any further.
executor = Executors.newSingleThreadExecutor();

executor.submit(new Runnable() {
@Override
public void run() {
long endTime = System.currentTimeMillis() + 10 * 1000;
while (System.currentTimeMillis() < endTime){
synchronized(this){
try{
wait(endTime - System.currentTimeMillis());
}catch(Exception e){e.printStackTrace();}
}
}

        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("myKey", "Button Pressed");
        msg.setData(bundle);
        handler.sendMessage(msg);
       
    }
});
executor.shutdownNow();
Step 15: Run the app and take a screenshot with “Button Pressed" in the TextView.
We will convert the Runnable to Callable to show how to obtain a Future object which is a value that will be provided in the future in an asynchronous way.
Step 16: Create a new button underneath the current existing “Submit” button in the layout file. Make sure you create sufficient constraints to prevent any UI issues.
On the attributes panel, define the text for the button as “Status”.
Step 17: Define Future variable and modify the buttonClick() with Callable as follows:
Step 17.1: Declare a Future variable in the variable area:
Future<String> future;
Just a reminder. Make sure you import the library as prompted when you type your code.
Step 17.2: Let's modify bottonClick() as follows:
public void buttonClick(View view){

    executor = Executors.newSingleThreadExecutor();

    future = executor.submit(new Callable<String>() {
        @Override
        public String call() {
            long endTime = System.currentTimeMillis() + 10 * 1000;
            while (System.currentTimeMillis() < endTime){
                synchronized(this){
                    try{
                        wait(endTime - System.currentTimeMillis());
                    }catch(Exception e){e.printStackTrace();}
                }
            }
            return("Task Completed");
        }
    });
}

Notice I took executor.shutdown() statement off.

Step 18: Define a new method, statusClick() as follows. Make sure you import the libraries while you type in the code.
public void statusClick(View view){
if (future.isDone()){
String result = null;
try {
result = future.get(300, TimeUnit.MILLISECONDS);
} catch(Exception e){
e.printStackTrace();
}
textView.setText(result);
executor.shutdownNow();
} else {
textView.setText("Waiting");
}

}
Step 19: Wire up the Status button in the onCreate() method
Step 19.1: Define the second button in the variable declaration area as follows:
Button button2;
Step 19.2: Put the following statements after where you define button in the onCreate() method
button2 = findViewById(R.id.button2);
button2.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
statusClick(view);
}
});
Notice here I assume your id for the second button is button2. If that is not the case for your app, you need to modify your code accordingly.

Step 20: Run the app. Click the Submit button. While the lengthy IO task is running, click “Status” button. Waiting message will display in the TextView. Take a screenshot. After the completion of the lengthy IO, that is, wait for 20 seconds, then press the Status button again, you should see “Task Competed” displayed in the TextView. Take another screenshot.
 
