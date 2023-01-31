package com.example.avcinteractivemapapp;

public class ContactItems {

    private String contactTitle;
    private String contactDescription;
    private String contactNumber;

    public ContactItems(String contactTitle, String contactDescription, String contactNumber) {

        this.contactTitle = contactTitle;
        this.contactDescription = contactDescription;
        this.contactNumber = contactNumber;

    }

    public String getContactTitle() {
        return contactTitle;
    }

    public String getContactDescription() {
        return contactDescription;
    }

    public String getContactNumber() {
        return contactNumber;
    }

   /* public void setContactItem(String ContactItem) {
        this.ContactItem = ContactItem;
    }*/

}
