(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('ProductAddressDeleteController',ProductAddressDeleteController);

    ProductAddressDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProductAddress'];

    function ProductAddressDeleteController($uibModalInstance, entity, ProductAddress) {
        var vm = this;

        vm.productAddress = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProductAddress.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
