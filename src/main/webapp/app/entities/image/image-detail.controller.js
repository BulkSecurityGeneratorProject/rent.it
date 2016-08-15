(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('ImageDetailController', ImageDetailController);

    ImageDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Image', 'Product'];

    function ImageDetailController($scope, $rootScope, $stateParams, entity, Image, Product) {
        var vm = this;

        vm.image = entity;

        var unsubscribe = $rootScope.$on('rentitApp:imageUpdate', function(event, result) {
            vm.image = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
