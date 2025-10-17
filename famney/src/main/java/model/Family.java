package model;

import java.io.Serializable;
import java.util.Date;

// Family class - Represents family groups in the Famney system
// Each family has a unique family code for member invitation
public class Family implements Serializable {
    
    private String familyId;
    private String familyCode; // Unique code like "FAMNEY-12345" for joining
    private String familyName;
    private String familyHead; // userId of the family head
    private Date createdDate;
    private Date lastModifiedDate;
    private boolean isActive;
    private int memberCount;
    
    // Constructor for creating new family
    public Family(String familyCode, String familyName, String familyHead) {
        this.familyCode = familyCode;
        this.familyName = familyName;
        this.familyHead = familyHead;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.memberCount = 1; // Family head is the first member
    }

    // Full constructor (for database retrieval)
    public Family(String familyId, String familyCode, String familyName, String familyHead, 
                  Date createdDate, Date lastModifiedDate, boolean isActive, int memberCount) {
        this.familyId = familyId;
        this.familyCode = familyCode;
        this.familyName = familyName;
        this.familyHead = familyHead;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.isActive = isActive;
        this.memberCount = memberCount;
    }
    
    // Default constructor
    public Family() {
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.memberCount = 0;
    }
    
    // Getters and Setters
    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyCode() {
        return familyCode;
    }

    public void setFamilyCode(String familyCode) {
        this.familyCode = familyCode;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyHead() {
        return familyHead;
    }

    public void setFamilyHead(String familyHead) {
        this.familyHead = familyHead;
        this.lastModifiedDate = new Date();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.lastModifiedDate = new Date();
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
        this.lastModifiedDate = new Date();
    }
    
    // Essential business logic methods only
    
    // Increment member count when new member joins
    public void incrementMemberCount() {
        this.memberCount++;
        this.lastModifiedDate = new Date();
    }
    
    // Decrement member count when member leaves
    public void decrementMemberCount() {
        if (this.memberCount > 1) {
            this.memberCount--;
            this.lastModifiedDate = new Date();
        }
    }
    
    // Generate display name for UI
    public String getDisplayName() {
        return familyName + " (" + familyCode + ")";
    }
    
    // Check if family has multiple members
    public boolean hasMultipleMembers() {
        return memberCount > 1;
    }

    // Get family age in days (useful for R1/R2 analytics)
    public long getFamilyAgeInDays() {
        if (createdDate == null) {
            return 0;
        }
        Date now = new Date();
        long diffInMillies = Math.abs(now.getTime() - createdDate.getTime());
        return diffInMillies / (24 * 60 * 60 * 1000);
    }
    
    @Override
    public String toString() {
        return "Family{" +
                "familyId='" + familyId + '\'' +
                ", familyCode='" + familyCode + '\'' +
                ", familyName='" + familyName + '\'' +
                ", familyHead='" + familyHead + '\'' +
                ", memberCount=" + memberCount +
                ", isActive=" + isActive +
                '}';
    }
}