(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('CommentDetailController', CommentDetailController);

    CommentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Comment', 'Product'];

    function CommentDetailController($scope, $rootScope, $stateParams, entity, Comment, Product) {
        var vm = this;

        vm.comment = entity;

        var unsubscribe = $rootScope.$on('rentitApp:commentUpdate', function(event, result) {
            vm.comment = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();