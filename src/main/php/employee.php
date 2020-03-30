<?php 

require_once('connection.php');

setlocale(LC_MONETARY, 'en_US');

if (!isset($_GET['employee_id'])) {

    // Retrieve list of employees
    $stmt = $conn->prepare("SELECT employee_id, first_name, last_name FROM employees ORDER BY first_name, last_name");
    $stmt->execute();
    
    echo "<form method='get'>";
    echo "<select name='employee_id' onchange='this.form.submit();'>";
    
    while ($row = $stmt->fetch()) {
        echo "<option value='$row[employee_id]'>$row[first_name] $row[last_name]</option>";
    }
    
    echo "</select>";
    echo "</form>";
} else {
    
    $employee_id = $_GET["employee_id"]; // GET NOT SAFE FOR PRIVACY OF VARIABLES
    
    //$stmt = $conn->prepare("SELECT employee_id, first_name, last_name, salary FROM employees WHERE employee_id=$employee_id"); // NOT SAFE FOR SQL INJECTION    
    $stmt = $conn->prepare("SELECT employee_id, first_name, last_name, salary FROM employees WHERE employee_id=:employee_id"); // PREPARED STATEMENT (BETTER USE)
    $stmt->bindValue(':employee_id', $employee_id);
    
    $stmt->execute();
    
    echo "<table style='border: solid 1px black;'>";
    echo "<thead><tr><th>ID</th><th>First name</th><th>Last name</th><th>Salary</th></tr></thead>";
    echo "<tbody>";
    
    $row = $stmt->fetch();    
    echo "<tr><td>$row[employee_id]</td><td>$row[first_name]</td><td>$row[last_name]</td><td>" . money_format("%.2n", $row["salary"]) . "</td></tr>";
        
    echo "</tbody>";
    echo "</table>";    
}

?>