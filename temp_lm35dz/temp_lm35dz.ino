void setup() {
  // put your setup code here, to run once:
  

  analogReference(INTERNAL);
}

void loop() {
  // put your main code here, to run repeatedly:
 
if(Serial){
  Serial.begin(9600);

  Serial.println((float) analogRead(A0)/9.31);

  Serial.end();

}

  delay (1000);
}
