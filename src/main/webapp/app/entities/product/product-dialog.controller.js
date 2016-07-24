(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('ProductDialogController', ProductDialogController);

    ProductDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Product', 'User', 'ProductAddress', 'Comment'];

    function ProductDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Product, User, ProductAddress, Comment) {
        var vm = this;

        vm.product = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();
        vm.productaddresses = ProductAddress.query({filter: 'product-is-null'});
        $q.all([vm.product.$promise, vm.productaddresses.$promise]).then(function() {
            if (!vm.product.productAddress || !vm.product.productAddress.id) {
                return $q.reject();
            }
            return ProductAddress.get({id : vm.product.productAddress.id}).$promise;
        }).then(function(productAddress) {
            vm.productaddresses.push(productAddress);
        });
        vm.comments = Comment.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.product.id !== null) {
                Product.update(vm.product, onSaveSuccess, onSaveError);
            } else {
                Product.save(vm.product, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:productUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
