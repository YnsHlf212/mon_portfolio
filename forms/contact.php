<<<<<<< HEAD
<?php
<?php
// Remplace cette adresse par la tienne
$to = "yanis.adidi@mediaschool.me";

// Vérifie que la requête est bien en POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Sécurise les entrées
    $name = htmlspecialchars(trim($_POST["name"] ?? ''));
    $email = filter_var(trim($_POST["email"] ?? ''), FILTER_VALIDATE_EMAIL);
    $subject = htmlspecialchars(trim($_POST["subject"] ?? ''));
    $message = htmlspecialchars(trim($_POST["message"] ?? ''));

    // Vérifie les champs obligatoires
    if ($name && $email && $subject && $message) {
        $headers = "From: $name <$email>\r\nReply-To: $email\r\n";
        $body = "Nom: $name\nEmail: $email\nSujet: $subject\n\n$message";
        if (mail($to, $subject, $body, $headers)) {
            echo "OK";
        } else {
            http_response_code(500);
            echo "Erreur lors de l'envoi du message.";
        }
    } else {
        http_response_code(400);
        echo "Veuillez remplir tous les champs correctement.";
    }
} else {
    http_response_code(405);
    echo "Méthode non autorisée.";
}
=======
<?php
<?php
// Remplace cette adresse par la tienne
$to = "yanis.adidi@mediaschool.me";

// Vérifie que la requête est bien en POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Sécurise les entrées
    $name = htmlspecialchars(trim($_POST["name"] ?? ''));
    $email = filter_var(trim($_POST["email"] ?? ''), FILTER_VALIDATE_EMAIL);
    $subject = htmlspecialchars(trim($_POST["subject"] ?? ''));
    $message = htmlspecialchars(trim($_POST["message"] ?? ''));

    // Vérifie les champs obligatoires
    if ($name && $email && $subject && $message) {
        $headers = "From: $name <$email>\r\nReply-To: $email\r\n";
        $body = "Nom: $name\nEmail: $email\nSujet: $subject\n\n$message";
        if (mail($to, $subject, $body, $headers)) {
            echo "OK";
        } else {
            http_response_code(500);
            echo "Erreur lors de l'envoi du message.";
        }
    } else {
        http_response_code(400);
        echo "Veuillez remplir tous les champs correctement.";
    }
} else {
    http_response_code(405);
    echo "Méthode non autorisée.";
}
>>>>>>> f52b067 (image et css)
?>