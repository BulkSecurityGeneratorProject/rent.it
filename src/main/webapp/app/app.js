/**
 * Created by alexandrgorbatov on 9/3/16.
 */
var app = angular.module("rentitApp", []);

app.directive('rentHeader', function () {
    return {
        restrict: 'E',
        templateUrl: 'layouts/header/header.html'
    };
});

app.directive('rentServices', function () {
    return {
        restrict: 'E',
        templateUrl: 'layouts/services/services.html'
    };
});

app.directive('rentProjects', function () {
    return {
        restrict: 'E',
        templateUrl: 'layouts/projects/projects.html'
    };
});

app.directive('rentContact', function () {
    return {
        restrict: 'E',
        templateUrl: 'layouts/contact/contact.html'
    };
});
