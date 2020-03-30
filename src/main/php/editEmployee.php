<?php 

require_once('connection.php');

session_start();

if (!isset($_GET['employee_id']) && $_SERVER['REQUEST_METHOD'] != 'POST') {
    
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
    exit();
}

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    
    $employee_id = $_GET["employee_id"];
    
    $stmt = $conn->prepare("SELECT first_name, last_name, email, salary FROM employees WHERE employee_id=:employee_id");
    $stmt->bindValue(':employee_id', $employee_id);
    
    $stmt->execute();
    
    $row = $stmt->fetch();

    echo "<form method='post' action='editEmployee.php'>";
    echo "<table style='border: solid 1px black;'>";
    echo "<tbody>";
    echo "<tr><td>First name</td><td><input name='first_name' type='text' size='25' value='$row[first_name]'></td></tr>";
    echo "<tr><td>Last name</td><td><input name='last_name' type='text' size='25' value='$row[last_name]'></td></tr>";
    echo "<tr><td>Email</td><td><input name='email' type='email' size='25' value='$row[email]'></td></tr>";
    echo "<tr><td>Salary</td><td><input name='salary' type='number' min='0.01' step='0.01' size='8' value='$row[salary]'></td></tr>";
    echo "<tr><td></td><td><input type='submit' value='Submit'></td></tr>";
    echo "</tbody>";
    echo "</table>";
    echo "</form>";
    
    $_SESSION["editEmployee_employee_id"] = $employee_id;    
    
} else {
    
    try {
        $stmt = $conn->prepare("UPDATE employees SET first_name=:first_name, last_name=:last_name, email=:email, salary=:salary WHERE employee_id=:employee_id");

        $stmt->bindValue(':first_name', $_POST['first_name']);
        $stmt->bindValue(':last_name', $_POST['last_name']);
        $stmt->bindValue(':email', $_POST['email']);
        $stmt->bindValue(':salary', $_POST['salary']);
        $stmt->bindValue(':employee_id', $_SESSION["editEmployee_employee_id"]);
        
        $stmt->execute();
    } catch (PDOException $e) {
        echo "Error: " . $e->getMessage();
    }
    
    unset ($_SESSION["editEmployee_employee_id"]);

    echo "Success";    
}

?>