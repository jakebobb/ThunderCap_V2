#include <SoftwareSerial.h> // use the software uart
SoftwareSerial bluetooth(2, 4); // RX, TX

unsigned long previousMillis = 0;        // will store last time
const long interval = 500;           // interval at which to delay
static uint32_t tmp; // increment this

void setup() {
  pinMode(13, OUTPUT); // for LED status
  bluetooth.begin(9600); // start the bluetooth uart at 9600 which is its default
  delay(200); // wait for voltage stabilize
  bluetooth.print("AT+NAMEmcuhq.com"); // place your name in here to configure the bluetooth name.
                                       // will require reboot for settings to take affect. 
  delay(3000); // wait for settings to take affect. 
}

void loop() {
  if (bluetooth.available()) { // check if anything in UART buffer
    if(bluetooth.read() == '1'){ // did we receive this character?
       digitalWrite(13,HIGH); // coil 1 activation
    }
    else if(bluetooth.read() == '2'){ // coil 2 activation
        digitalWrite(14,HIGH);
      }
    else if(bluetooth.read() == '3'){ //coil 3 activation
        digitalWrite(15,HIGH);
      }
      else if(bluetooth.read() == '4'){ // coil 4 activation
        digitalWrite(16,HIGH);
      }
  }
  
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    bluetooth.print(tmp++); // print this to bluetooth module
  }

}
