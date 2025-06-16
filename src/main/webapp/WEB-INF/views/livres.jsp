<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Livres - Gestionnaire de Bibliothèque</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
    <script>
        // Script pour ajouter l'animation de mise en évidence
        document.addEventListener('DOMContentLoaded', function() {
            // Vérifier si l'URL contient le paramètre pour les livres disponibles
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('action') === 'disponibles') {
                // Attendre un peu pour que la page soit chargée
                setTimeout(function() {
                    const tableContainer = document.querySelector('#tableau-livres .table-container');
                    if (tableContainer) {
                        tableContainer.classList.add('highlight-table');
                        // Retirer la classe après l'animation
                        setTimeout(function() {
                            tableContainer.classList.remove('highlight-table');
                        }, 2000);
                    }
                }, 500);
            }
        });
    </script>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📖 Gestion des Livres</h1>
            <p>Gérez votre collection de livres facilement</p>
            <a href="${pageContext.request.contextPath}/" class="nav-link">← Retour à l'accueil</a>
        </div>

        <!-- Messages de notification -->
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

        <!-- Formulaire d'ajout de livre -->
        <div class="card">
            <h2>➕ Ajouter un nouveau livre</h2>
            <form action="${pageContext.request.contextPath}/livres" method="post">
                <input type="hidden" name="action" value="add"/>
                <div class="form-group">
                    <input type="text" name="titre" class="form-control" placeholder="Titre du livre" required/>
                    <input type="text" name="auteur" class="form-control" placeholder="Auteur du livre" required/>
                    <button type="submit" class="btn btn-primary">Ajouter le livre</button>
                </div>
            </form>
        </div>

        <!-- Section de recherche -->
        <div class="card">
            <h2>🔍 Recherche et filtres</h2>
            <form method="get" action="${pageContext.request.contextPath}/livres">
                <input type="hidden" name="action" value="recherche"/>
                <div class="search-section">
                    <input type="text" name="terme" class="form-control" 
                           placeholder="Rechercher par titre ou auteur..." 
                           value="${terme != null ? terme : ''}"/>
                    <button type="submit" class="btn btn-primary">Rechercher</button>
                    <a href="${pageContext.request.contextPath}/livres" class="btn btn-secondary">Réinitialiser</a>
                </div>
            </form>
            
            <div class="quick-actions">
                <a href="${pageContext.request.contextPath}/livres?action=disponibles#tableau-livres" class="btn btn-primary btn-small">
                    ✅ Livres disponibles uniquement
                </a>
            </div>
        </div>

        <!-- Tableau des livres avec ancrage -->
        <div class="card table-anchor" id="tableau-livres">
            <h2>📚 Liste des livres
                <c:if test="${param.action == 'disponibles'}">
                    <span style="font-size: 0.7em; color: #28a745;">(Disponibles uniquement)</span>
                </c:if>
            </h2>
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Titre</th>
                            <th>Auteur</th>
                            <th>Disponibilité</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="livre" items="${livres}">
                            <tr>
                                <td><strong>#${livre.id}</strong></td>
                                <td>${livre.titre}</td>
                                <td>${livre.auteur}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${livre.disponible}">
                                            <span class="status-badge status-available">Disponible</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-unavailable">Emprunté</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="actions">
                                        <a href="${pageContext.request.contextPath}/livres?action=edit&id=${livre.id}" 
                                           class="btn btn-edit btn-small">Modifier</a>
                                        <a href="${pageContext.request.contextPath}/livres?action=delete&id=${livre.id}" 
                                           class="btn btn-delete btn-small"
                                           onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce livre ?');">Supprimer</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty livres}">
                            <tr>
                                <td colspan="5">
                                    <div class="empty-state">
                                        <span class="icon">📭</span>
                                        <h3>Aucun livre trouvé</h3>
                                        <p>
                                            <c:choose>
                                                <c:when test="${param.action == 'disponibles'}">
                                                    Aucun livre disponible pour le moment
                                                </c:when>
                                                <c:otherwise>
                                                    Ajoutez votre premier livre ou modifiez vos critères de recherche
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Formulaire de modification de livre -->
        <c:if test="${not empty livre}">
            <div class="card">
                <h2>✏️ Modifier le livre</h2>
                <form action="${pageContext.request.contextPath}/livres" method="post">
                    <input type="hidden" name="action" value="update"/>
                    <input type="hidden" name="id" value="${livre.id}"/>
                    <div class="form-group">
                        <input type="text" name="titre" class="form-control" placeholder="Titre du livre" value="${livre.titre}" required/>
                        <input type="text" name="auteur" class="form-control" placeholder="Auteur du livre" value="${livre.auteur}" required/>
                        <div style="display: flex; align-items: center; gap: 10px;">
                            <label style="display: flex; align-items: center; gap: 5px;">
                                <input type="checkbox" name="disponible" <c:if test="${livre.disponible}">checked</c:if> />
                                Disponible
                            </label>
                        </div>
                    </div>
                    <div style="display: flex; gap: 15px; justify-content: center; margin-top: 20px;">
                        <button type="submit" class="btn btn-primary">💾 Enregistrer</button>
                        <a href="${pageContext.request.contextPath}/livres" class="btn btn-secondary">❌ Annuler</a>
                    </div>
                </form>
            </div>
        </c:if>

        <div class="footer">
            <p>&copy; 2025 Gestionnaire de Bibliothèque - Module Gestion des Livres</p>
        </div>
    </div>
</body>
</html>
