package com.ensias.ebarber.model;

 public   class Symptom {
  public   String type="Absent",symptom1="Absent",symptom2="Absent",symptom3="Absent";
    public Symptom(String Type,String Symptom1,String Symptom2,String Symptom3){
      type=Type;
      symptom1=Symptom1;
      symptom2=Symptom2;
      symptom3=Symptom3;
    }
    public Symptom getObject(){
        return new Symptom(type,symptom1,symptom2,symptom3);
    }

     public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }

     public String getSymptom1() {
         return symptom1;
     }

     public void setSymptom1(String symptom1) {
         this.symptom1 = symptom1;
     }

     public String getSymptom2() {
         return symptom2;
     }

     public void setSymptom2(String symptom2) {
         this.symptom2 = symptom2;
     }

     public String getSymptom3() {
         return symptom3;
     }

     public void setSymptom3(String symptom3) {
         this.symptom3 = symptom3;
     }
 }
