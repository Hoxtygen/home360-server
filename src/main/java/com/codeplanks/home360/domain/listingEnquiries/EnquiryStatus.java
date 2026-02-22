/* (C)2025-2026 */
package com.codeplanks.home360.domain.listingEnquiries;

public enum EnquiryStatus {
  PENDING, // New Lead, not yet acknowledged
  ACTIVE, // Ongoing conversation
  ARCHIVED, // Agent has finished/closed the lead
  COMPLETED // Lead resulted in a successful rental/sale
}
