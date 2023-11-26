package com.cst2335.budgetease;


    public class Expense {
        private int id;
        private double amount;
        private String category;
        private String date;

        public Expense() {

        }

        public String getNote() {
            return note;
        }

        public Expense(int id, double amount, String category, String date, String note) {
            this.id = id;
            this.amount = amount;
            this.category = category;
            this.date = date;
            this.note = note;
        }
        public void setNote(String note) {
            this.note = note;
        }

        private String note;

        public int getId (){
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public double getAmount (){
         return amount;
        }
        public void setAmount(Double amount){
            this.amount=amount;
        }

        public String getCategory (){
            return category;
        }
        public void setCategory(String category){

            this.category=category;
        }

        public String getDate (){
            return date;
        }
        public void setDate(String date){
            this.date=date;
        }



    }


