(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('AppSettingsDeleteController',AppSettingsDeleteController);

    AppSettingsDeleteController.$inject = ['$uibModalInstance', 'entity', 'AppSettings'];

    function AppSettingsDeleteController($uibModalInstance, entity, AppSettings) {
        var vm = this;

        vm.appSettings = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            AppSettings.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
