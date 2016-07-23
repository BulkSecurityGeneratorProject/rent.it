(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('UserAddressDeleteController',UserAddressDeleteController);

    UserAddressDeleteController.$inject = ['$uibModalInstance', 'entity', 'UserAddress'];

    function UserAddressDeleteController($uibModalInstance, entity, UserAddress) {
        var vm = this;

        vm.userAddress = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            UserAddress.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
