(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('BookingDialogController', BookingDialogController);

    BookingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Booking', 'User', 'Product', 'TimeSlot'];

    function BookingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Booking, User, Product, TimeSlot) {
        var vm = this;

        vm.booking = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();
        vm.products = Product.query({filter: 'booking-is-null'});
        $q.all([vm.booking.$promise, vm.products.$promise]).then(function() {
            if (!vm.booking.product || !vm.booking.product.id) {
                return $q.reject();
            }
            return Product.get({id : vm.booking.product.id}).$promise;
        }).then(function(product) {
            vm.products.push(product);
        });
        vm.timeslots = TimeSlot.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.booking.id !== null) {
                Booking.update(vm.booking, onSaveSuccess, onSaveError);
            } else {
                Booking.save(vm.booking, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('rentitApp:bookingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
