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
            <div class="alert alert-success" role="alert" aria-live="polite">
                ✅ <c:out value="${message}"/>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error" role="alert" aria-live="assertive">
                ❌ <c:out value="${error}"/>
            </div>
        </c:if>

        <!-- Section AJOUT/ÉDITION -->
        <div class="section ${not empty utilisateur ? 'section-edit' : 'section-add'}">
            <div class="section-header">
                <h2 class="section-title">
                    <span class="section-icon">${not empty utilisateur ? '✏️' : '➕'}</span>
                    ${not empty utilisateur ? 'MODIFIER UN UTILISATEUR' : 'AJOUTER UN UTILISATEUR'}
                </h2>
                <p class="section-description">
                    ${not empty utilisateur ? 'Modifiez les informations de l\'utilisateur' : 'Ajoutez un nouveau membre à votre bibliothèque'}
                </p>
            </div>
            <div class="card ${not empty utilisateur ? 'card-edit' : 'card-add'}">
                <form action="${pageContext.request.contextPath}/utilisateurs" method="post" novalidate>
                    <input type="hidden" name="action" value="${not empty utilisateur ? 'update' : 'add'}"/>
                    <c:if test="${not empty utilisateur}">
                        <input type="hidden" name="id" value="<c:out value="${utilisateur.id}"/>"/>
                    </c:if>
                    <%= com.bibliotheque.config.CSRFUtil.getHiddenField(request) %>
                    <div class="form-grid">
                        <div class="form-field">
                            <label for="nom">👤 Nom complet *</label>
                            <input type="text" id="nom" name="nom" class="form-control"
                                   placeholder="Ex: Jean Dupont"
                                   value="<c:out value="${not empty utilisateur ? utilisateur.nom : ''}"/>"
                                   required aria-required="true"/>
                        </div>
                        <div class="form-field">
                            <label for="email">📧 Adresse email *</label>
                            <input type="email" id="email" name="email" class="form-control"
                                   placeholder="Ex: jean.dupont@email.com"
                                   value="<c:out value="${not empty utilisateur ? utilisateur.email : ''}"/>"
                                   required aria-required="true"/>
                        </div>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn ${not empty utilisateur ? 'btn-warning' : 'btn-success'} btn-large">
                            ${not empty utilisateur ? '💾 Enregistrer les modifications' : '➕ Ajouter l\'utilisateur'}
                        </button>
                        <c:if test="${not empty utilisateur}">
                            <a href="${pageContext.request.contextPath}/utilisateurs" class="btn btn-secondary">❌ Annuler</a>
                        </c:if>
                    </div>
                </form>
            </div>
        </div>

        <!-- Section LISTE DES UTILISATEURS -->
        <div class="section section-collection">
            <div class="section-header">
                <h2 class="section-title">
                    <span class="section-icon">👥</span>
                    MEMBRES DE LA BIBLIOTHÈQUE
                </h2>
                <p class="section-description">Consultez et gérez tous les membres inscrits</p>
            </div>
            <div class="card card-collection" id="utilisateurs">
                <div class="table-container">
                    <table role="table" aria-label="Liste des utilisateurs">
                        <thead>
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col">Nom</th>
                                <th scope="col">Email</th>
                                <th scope="col">Date d'inscription</th>
                                <th scope="col">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="utilisateur" items="${utilisateurs}">
                                <tr>
                                    <td><strong>#<c:out value="${utilisateur.id}"/></strong></td>
                                    <td><c:out value="${utilisateur.nom}"/></td>
                                    <td><c:out value="${utilisateur.email}"/></td>
                                    <td><c:out value="${utilisateur.dateInscription}"/></td>
                                    <td>
                                        <div class="actions">
                                            <a href="${pageContext.request.contextPath}/utilisateurs?action=edit&id=${utilisateur.id}"
                                               class="btn btn-edit btn-small">Modifier</a>
                                            <form method="post" action="${pageContext.request.contextPath}/utilisateurs" style="display:inline;">
                                                <input type="hidden" name="action" value="delete"/>
                                                <input type="hidden" name="id" value="<c:out value="${utilisateur.id}"/>"/>
                                                <%= com.bibliotheque.config.CSRFUtil.getHiddenField(request) %>
                                                <button type="submit" class="btn btn-delete btn-small"
                                                        onclick="return confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?');">Supprimer</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty utilisateurs}">
                                <tr>
                                    <td colspan="5">
                                        <div class="empty-state">
                                            <span class="icon">📭</span>
                                            <h3>Aucun utilisateur trouvé</h3>
                                            <p>Ajoutez votre premier membre pour commencer</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="footer">
            <p>&copy; 2025 Gestionnaire de Bibliothèque - Module Gestion des Utilisateurs</p>
        </div>
    </div>
</body>
</html>