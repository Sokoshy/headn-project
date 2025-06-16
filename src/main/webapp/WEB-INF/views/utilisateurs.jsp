<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Utilisateurs - Gestionnaire de Bibliothèque</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>👥 Gestion des Utilisateurs</h1>
            <p>Gérez les membres de votre bibliothèque</p>
            <a href="${pageContext.request.contextPath}/" class="nav-link">← Retour à l'accueil</a>
        </div>

        <c:if test="${not empty message}">
            <div class="alert alert-success">
                ✅ ${message}
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ❌ ${error}
            </div>
        </c:if>

        <div class="card">
            <c:choose>
                <c:when test="${not empty utilisateur}">
                    <h2>✏️ Modifier l'utilisateur</h2>
                    <form action="${pageContext.request.contextPath}/utilisateurs" method="post">
                        <input type="hidden" name="action" value="update"/>
                        <input type="hidden" name="id" value="${utilisateur.id}"/>
                        <div class="form-group-edit">
                            <input type="text" name="nom" class="form-control" 
                                   placeholder="Nom complet" value="${utilisateur.nom}" required/>
                            <input type="email" name="email" class="form-control" 
                                   placeholder="Adresse email" value="${utilisateur.email}" required/>
                        </div>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">💾 Enregistrer les modifications</button>
                            <a href="${pageContext.request.contextPath}/utilisateurs" class="btn btn-secondary">❌ Annuler</a>
                        </div>
                    </form>
                </c:when>
                <c:otherwise>
                    <h2>➕ Ajouter un nouvel utilisateur</h2>
                    <form action="${pageContext.request.contextPath}/utilisateurs" method="post">
                        <input type="hidden" name="action" value="add"/>
                        <div class="form-group">
                            <input type="text" name="nom" class="form-control" 
                                   placeholder="Nom complet" required/>
                            <input type="email" name="email" class="form-control" 
                                   placeholder="Adresse email" required/>
                            <button type="submit" class="btn btn-primary">Ajouter l'utilisateur</button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="card">
            <h2>📋 Liste des utilisateurs</h2>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nom</th>
                            <th>Email</th>
                            <th>Date d'inscription</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="utilisateurItem" items="${utilisateurs}">
                            <tr>
                                <td><strong>#${utilisateurItem.id}</strong></td>
                                <td>${utilisateurItem.nom}</td>
                                <td><span class="user-email">${utilisateurItem.email}</span></td>
                                <td><span class="user-date">${utilisateurItem.dateInscription}</span></td>
                                <td>
                                    <div class="actions">
                                        <a href="${pageContext.request.contextPath}/utilisateurs?action=edit&id=${utilisateurItem.id}" 
                                           class="btn btn-edit btn-small">Modifier</a>
                                        <a href="${pageContext.request.contextPath}/utilisateurs?action=delete&id=${utilisateurItem.id}" 
                                           class="btn btn-delete btn-small"
                                           onclick="return confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?');">Supprimer</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty utilisateurs}">
                            <tr>
                                <td colspan="5">
                                    <div class="empty-state">
                                        <span class="icon">👤</span>
                                        <h3>Aucun utilisateur trouvé</h3>
                                        <p>Ajoutez votre premier utilisateur pour commencer</p>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="footer">
            <p>&copy; 2025 Gestionnaire de Bibliothèque - Module Gestion des Utilisateurs</p>
        </div>
    </div>
</body>
</html>
