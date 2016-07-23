(function() {
    'use strict';

    angular
        .module('rentitApp')
        .controller('BookingDetailController', BookingDetailController);

    BookingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Booking', 'Product', 'TimeSlot'];

    function BookingDetailController($scope, $rootScope, $stateParams, entity, Booking, Product, TimeSlot) {
        var vm = this;

        vm.booking = entity;

        var unsubscribe = $rootScope.$on('rentitApp:bookingUpdate', function(event, result) {
            vm.booking = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
