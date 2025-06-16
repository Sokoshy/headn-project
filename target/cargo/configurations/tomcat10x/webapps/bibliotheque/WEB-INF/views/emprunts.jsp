<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Emprunts - Gestionnaire de Bibliothèque</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📋 Gestion des Emprunts</h1>
            <p>Suivez et gérez les emprunts de livres de votre bibliothèque</p>
            <a href="${pageContext.request.contextPath}/" class="nav-link">← Retour à l'accueil</a>
        </div>

        <!-- Messages d'alerte -->
        <c:if test="${not empty message}">
            <div class="alert success">
                ✅ ${message}
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert error">❌ ${error}</div>
        </c:if>

        <!-- Formulaire nouvel emprunt -->
        <div class="card">
            <h2>📚 Nouvel Emprunt</h2>
            <form action="${pageContext.request.contextPath}/emprunts" method="post">
                <input type="hidden" name="action" value="add"/>
                <div class="form-row">
                    <div class="form-group">
                        <label for="utilisateurId">👤 Utilisateur :</label>
                        <select name="utilisateurId" id="utilisateurId" class="form-control" required>
                            <option value="">-- Sélectionner un utilisateur --</option>
                            <c:forEach var="u" items="${utilisateurs}">
                                <option value="${u.id}">${u.nom} (${u.email})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="livreId">📖 Livre :</label>
                        <select name="livreId" id="livreId" class="form-control" required>
                            <option value="">-- Sélectionner un livre --</option>
                            <c:forEach var="l" items="${livresDisponibles}">
                                <option value="${l.id}">${l.titre} (${l.auteur})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary">Emprunter</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- Filtre par utilisateur -->
        <div class="card">
            <h2>🔍 Filtrer les Emprunts</h2>
            <div class="filter-section">
                <form method="get" action="${pageContext.request.contextPath}/emprunts">
                    <input type="hidden" name="action" value="list"/>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="filtreUtilisateur">Filtrer par utilisateur :</label>
                            <select name="filtreUtilisateur" id="filtreUtilisateur" class="form-control" onchange="this.form.submit()">
                                <option value="">-- Tous les utilisateurs --</option>
                                <c:forEach var="u" items="${utilisateurs}">
                                    <option value="${u.id}" <c:if test="${param.filtreUtilisateur == u.id}">selected</c:if>>
                                        ${u.nom} (${u.email})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div></div>
                        <div class="form-group">
                            <noscript><button type="submit" class="btn btn-primary">Filtrer</button></noscript>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Tableau des emprunts -->
        <div class="card">
            <h2>📊 Liste des Emprunts</h2>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Utilisateur</th>
                            <th>Email</th>
                            <th>Livre</th>
                            <th>Auteur</th>
                            <th>Date Emprunt</th>
                            <th>Date Retour</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="emprunt" items="${emprunts}">
                            <!-- Supprimez cette ligne qui était commentée et qui causait le problème -->
                            <!-- <c:if test="${empty param.filtreUtilisateur || param.filtreUtilisateur == emprunt.utilisateurId}"> -->
                            
                            <tr>
                                <td><strong>#${emprunt.id}</strong></td>
                                <td>${emprunt.nomUtilisateur}</td>
                                <td>${emprunt.emailUtilisateur}</td>
                                <td><strong>${emprunt.titreLivre}</strong></td>
                                <td><em>${emprunt.auteurLivre}</em></td>
                                <td>${emprunt.dateEmprunt}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty emprunt.dateRetour}">
                                            ${emprunt.dateRetour}
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #e74c3c; font-style: italic;">En cours</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty emprunt.dateRetour}">
                                            <span class="status-badge status-disponible">Rendu</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-emprunte">Emprunté</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${empty emprunt.dateRetour}">
                                        <form action="${pageContext.request.contextPath}/emprunts" method="post" style="display:inline">
                                            <input type="hidden" name="action" value="retour"/>
                                            <input type="hidden" name="empruntId" value="${emprunt.id}"/>
                                            <button type="submit" class="btn btn-success">↩️ Retour</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                            
                            <!-- Supprimez aussi cette ligne de fermeture -->
                            <!-- </c:if> -->
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Liste des livres disponibles -->
        <div class="card">
            <h2>✅ Livres Disponibles</h2>
            <div class="books-grid">
                <c:forEach var="l" items="${livresDisponibles}">
                    <div class="book-item">
                        <div class="book-title">📖 ${l.titre}</div>
                        <div class="book-author">✍️ ${l.auteur}</div>
                        <span class="status-badge status-disponible">Disponible</span>
                    </div>
                </c:forEach>
            </div>
            <c:if test="${empty livresDisponibles}">
                <div style="text-align: center; color: #7f8c8d; padding: 40px;">
                    <h3>📚 Aucun livre disponible</h3>
                    <p>Tous les livres sont actuellement empruntés.</p>
                </div>
            </c:if>
        </div>

        <div class="header" style="margin-top: 30px; padding: 20px;">
            <p style="color: #7f8c8d;">&copy; 2025 Gestionnaire de Bibliothèque - Projet Éducatif</p>
        </div>
    </div>
</body>
</html>
