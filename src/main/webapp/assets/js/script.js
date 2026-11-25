/**
 * Library Management System - JavaScript Interactions
 */

// Confirmation dialog before deleting a record
function confirmDelete(entityName, itemName) {
    var message = "Are you sure you want to delete this " + entityName;
    if (itemName) {
        message += " (\"" + itemName + "\")";
    }
    message += "?\n\nThis action cannot be undone.";
    return confirm(message);
}

// Client-side validation for Book Form
function validateBookForm(form) {
    var title = form.title.value.trim();
    var author = form.author.value.trim();
    var category = form.category.value.trim();
    var qty = parseInt(form.totalQuantity.value, 10);

    if (!title) {
        alert("Please enter the book title.");
        form.title.focus();
        return false;
    }
    if (!author) {
        alert("Please enter the author name.");
        form.author.focus();
        return false;
    }
    if (!category) {
        alert("Please enter or select a category.");
        form.category.focus();
        return false;
    }
    if (isNaN(qty) || qty <= 0) {
        alert("Total quantity must be a positive number greater than 0.");
        form.totalQuantity.focus();
        return false;
    }
    return true;
}

// Client-side validation for Member Form
function validateMemberForm(form) {
    var name = form.name.value.trim();
    var email = form.email.value.trim();
    var phone = form.phone.value.trim();

    if (!name) {
        alert("Please enter the member's full name.");
        form.name.focus();
        return false;
    }
    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !emailRegex.test(email)) {
        alert("Please enter a valid email address (e.g., user@example.com).");
        form.email.focus();
        return false;
    }
    var phoneRegex = /^[0-9+ -]{7,15}$/;
    if (!phone || !phoneRegex.test(phone)) {
        alert("Please enter a valid phone number (at least 7 digits).");
        form.phone.focus();
        return false;
    }
    return true;
}

// Client-side validation for Issue Book Form
function validateIssueForm(form) {
    if (!form.bookId.value) {
        alert("Please select a book to issue.");
        form.bookId.focus();
        return false;
    }
    if (!form.memberId.value) {
        alert("Please select a member.");
        form.memberId.focus();
        return false;
    }
    return true;
}

// Dismiss alert banner
function dismissAlert(btn) {
    var alertBox = btn.closest(".alert");
    if (alertBox) {
        alertBox.style.display = "none";
    }
}

// Auto dismiss alerts after 6 seconds
document.addEventListener("DOMContentLoaded", function () {
    var alerts = document.querySelectorAll(".alert");
    if (alerts.length > 0) {
        setTimeout(function () {
            alerts.forEach(function (alert) {
                alert.style.transition = "opacity 0.5s ease";
                alert.style.opacity = "0";
                setTimeout(function () {
                    alert.style.display = "none";
                }, 500);
            });
        }, 6000);
    }
});
