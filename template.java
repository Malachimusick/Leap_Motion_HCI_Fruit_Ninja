import java.awt.Robot;
import com.leapmotion.leap.*;
import java.awt.event.*;
import com.leapmotion.leap.Gesture.State;
import java.awt.Dimension;
import java.awt.AWTException;
import java.awt.PointerInfo;
import java.awt.Point;
import java.awt.MouseInfo;


class CustomListener extends Listener {
  // The automator. More in the information document under "Leap Motion 101"
  public Robot robot;
  private int counter = 0;
  private static int APPLICATION_MARGIN = 10;
  private static double SENSITIVITY = 0.5;

  // Executes once your program and detector are connected, are there any specific gestures you're going
  // to look for later on? Uncomment the ones you want to enable below
  public void onConnect(Controller controller) {
    System.out.println("Connected");
    controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
    controller.enableGesture(Gesture.Type.TYPE_SWIPE);
    controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
  //  controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES); // allows the program to run in places other than the terminal
  }

  // Executes when detector stops working/is no longer connected to computer
  public void onDisconnect(Controller controller) {
    System.out.println("Disconnected");
  }

  // Executes when you manually stop running the detector/program
  public void onExit(Controller controller) {
    System.out.println("Exited");
  }

  // More info in the Leap Motion 101 section
  public void onFrame(Controller controller) {
    try {
    // Creating our robot friend. Feel free to name it anything you'd like
      robot = new Robot();
    } catch(Exception e) {}


    /*––––––––––––*/
    /* CODE HERE. */
    /*––––––––––––*/
    //getFrameLogs(frame);

    Frame frame = controller.frame();
    GestureList gestures = frame.gestures();
    InteractionBox box = frame.interactionBox();

    if (frame.hands().isEmpty()) {
        System.out.println("No hands.. " + counter);
        counter++;
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    else{

        counter++;

        for (Finger finger : frame.fingers()){
          if(finger.type() == Finger.Type.TYPE_INDEX){
            Vector fingerPOS = finger.stabilizedTipPosition();
            Vector fingerBoxPOS = box.normalizePoint(fingerPOS);
            Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

            int newXPos = (int) (screen.width * fingerBoxPOS.getX());
            int newYPos = (int) (screen.height - fingerBoxPOS.getY() * screen.height);

            robot.mouseMove(newXPos, newYPos);

            //get mouse location
            PointerInfo a = MouseInfo.getPointerInfo();
            Point b = a.getLocation();
            int mousePositionX = (int) b.getX();
            int mousePositionY = (int) b.getY();

            if(!(mousePositionX > screen.width - APPLICATION_MARGIN
               || mousePositionX < APPLICATION_MARGIN
               || mousePositionY > screen.height - APPLICATION_MARGIN
               || mousePositionY < APPLICATION_MARGIN
               )){

                 robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                 System.out.println("mouse pressing.. " + counter);
            }else{
                 robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }
          }
        }

  }



  }

  private void getFrameLogs(Frame frame){

    for(Hand hand : frame.hands()) {
        String handType = hand.isLeft() ? "Left hand" : "Right hand";
        System.out.println("  " + handType + ", id: " + hand.id()
                         + ", palm position: " + hand.palmPosition());

        // Get the hand's normal vector and direction
        Vector normal = hand.palmNormal();
        Vector direction = hand.direction();

        // Calculate the hand's pitch, roll, and yaw angles
        System.out.println("  pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
                         + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
                         + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");

        // Get arm bone
        Arm arm = hand.arm();
        System.out.println("  Arm direction: " + arm.direction()
                         + ", wrist position: " + arm.wristPosition()
                         + ", elbow position: " + arm.elbowPosition());

        // Get fingers
        for (Finger finger : hand.fingers()) {
            System.out.println("    " + finger.type() + ", id: " + finger.id()
                             + ", length: " + finger.length()
                             + "mm, width: " + finger.width() + "mm");

            //Get Bones
            for(Bone.Type boneType : Bone.Type.values()) {
                Bone bone = finger.bone(boneType);
                System.out.println("      " + bone.type()
                                 + " bone, start: " + bone.prevJoint()
                                 + ", end: " + bone.nextJoint()
                                 + ", direction: " + bone.direction());
            }
        }
    }
  }


} // end of listener class



// The "main()" function
// NAME THIS CLASS WHATEVER YOUR FILE IS NAMED
class template {
  public static void main(String[] args){
    // initializes our detector
    CustomListener listener = new CustomListener();
    Controller controller = new Controller();
    controller.addListener(listener);
    System.out.println("Press Enter to quit...");

    try {
      System.in.read();
    } catch(Exception e) {}
    controller.removeListener(listener);
  }
}
