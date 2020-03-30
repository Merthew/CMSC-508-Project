<?php 

require_once('connection.php');

// Show all employees

$stmt = $conn->prepare("SELECT employee_id, first_name, last_name FROM employees ORDER BY first_name, last_name");
$stmt->execute();

echo "<table style='border: solid 1px black;'>";
echo "<thead><tr><th>ID</th><th>First name</th><th>Last name</th></tr></thead>";
echo "<tbody>";

while ($row = $stmt->fetch()) {
    echo "<tr><td>$row[employee_id]</td><td>$row[first_name]</td><td>$row[last_name]</td></tr>";
}

echo "</tbody>";
echo "</table>";

?>